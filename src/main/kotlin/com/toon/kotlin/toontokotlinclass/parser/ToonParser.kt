package com.toon.kotlin.toontokotlinclass.parser

/**
 * Parser for TOON (Tree Object Oriented Notation) format.
 * 
 * Converts indentation-based TOON text into an Abstract Syntax Tree (AST)
 * represented by ToonNode objects.
 * 
 * Supported TOON patterns:
 * - Object declaration: `key:`
 * - Property: `key: value`
 * - Simple list: `key[n]: item1,item2,item3`
 * - Object list: `key[n]{header1,header2}: row1col1,row1col2`
 * 
 * @see ToonNode for AST node types
 * @see ToonValidator for input validation
 */
class ToonParser {

    /**
     * Parses TOON format text into a map of top-level objects.
     * 
     * @param input The TOON format text to parse
     * @return Map of object names to their corresponding ObjectNode
     * @throws EmptyInputException if input is blank
     * @throws InvalidToonSyntaxException if syntax is malformed
     * @throws InvalidIndentationException if indentation is inconsistent
     * @throws DuplicateKeyException if duplicate keys found at same level
     * @throws SchemaMismatchException if object list data doesn't match schema
     */
    fun parse(input: String): Map<String, ToonNode.ObjectNode> {
        
        // Validate input first
        ToonValidator.validate(input)

        val tokenizer = ToonTokenizer()
        val tokens = tokenizer.tokenize(input)

        val result = mutableMapOf<String, ToonNode.ObjectNode>()
        val stack = ArrayDeque<MutableList<ToonNode>>()

        var lastIndent = 0
        
        // Track keys at current level to detect duplicates
        val keysAtLevel = mutableMapOf<Int, MutableSet<String>>()

        var i = 0
        while (i < tokens.size) {
            val (indent, line) = tokens[i]

            // Move up levels
            while (indent < lastIndent) {
                stack.removeLast()
                lastIndent--
            }

            // ====================================================
            // CASE 1 — Object start: key:
            // ====================================================
            if (line.endsWith(":") && !line.contains("[")) {

                val key = line.removeSuffix(":")
                ToonValidator.validateKey(key, i + 1)

                // Check if this is a TOP-LEVEL object (indent = 0) or NESTED object (indent > 0)
                if (indent == 0) {
                    // Top-level object: create new root node
                    val rootNode = ToonNode.ObjectNode(key)
                    result[key] = rootNode

                    stack.clear()
                    stack += rootNode.children

                    lastIndent = indent + 1
                    
                    // Reset key tracking for new object
                    keysAtLevel.clear()
                } else {
                    // Nested object: add to parent's children
                    val nestedNode = ToonNode.ObjectNode(key)
                    
                    // Check for duplicates at this level
                    val levelKeys = keysAtLevel.getOrPut(indent) { mutableSetOf() }
                    ToonValidator.checkDuplicateKey(levelKeys, key, i + 1)
                    levelKeys.add(key)
                    
                    if (stack.isNotEmpty()) {
                        stack.last().add(nestedNode)
                        
                        // Push this nested object's children onto the stack
                        stack.addLast(nestedNode.children)
                        lastIndent = indent + 1
                    } else {
                        throw InvalidToonSyntaxException(
                            "Nested object '$key' found without a parent object",
                            i + 1
                        )
                    }
                }
                
                i++
                continue
            }

            // ====================================================
            // CASE 2 — Simple property: key: value
            // ====================================================
            if (":" in line && !line.contains("[") && !line.contains("{")) {

                val parts = line.split(":", limit = 2)
                if (parts.size != 2) {
                    throw InvalidToonSyntaxException("Invalid property format: '$line'", i + 1)
                }
                
                val key = parts[0].trim()
                val rawValue = parts[1].trim()
                
                ToonValidator.validateKey(key, i + 1)
                
                // Check for duplicates at this level
                val levelKeys = keysAtLevel.getOrPut(indent) { mutableSetOf() }
                ToonValidator.checkDuplicateKey(levelKeys, key, i + 1)
                levelKeys.add(key)
                
                val value = TypeInference.inferValue(rawValue)

                if (stack.isNotEmpty()) {
                    stack.last().add(ToonNode.Property(key, value))
                }

                lastIndent = indent
                i++
                continue
            }

            // ====================================================
            // CASE 3 — Simple list: key[n]: a,b,c
            // ====================================================
            if (line.matches(Regex("^.+\\[\\d+]:\\s*.+"))) {

                val key = line.substringBefore("[")
                ToonValidator.validateKey(key, i + 1)
                
                // Check for duplicates at this level
                val levelKeys = keysAtLevel.getOrPut(indent) { mutableSetOf() }
                ToonValidator.checkDuplicateKey(levelKeys, key, i + 1)
                levelKeys.add(key)
                
                val itemsRaw = line.substringAfter("]:")
                val items = itemsRaw.split(",").map { it.trim() }

                if (stack.isNotEmpty()) {
                    stack.last().add(ToonNode.ListNode(key, items))
                }

                lastIndent = indent
                i++
                continue
            }

            // ====================================================
            // CASE 4 — Object list header: key[n]{schema}:
            //       Example: hikes[3]{id,name,distance}:
            // ====================================================
            if (line.matches(Regex(".+\\[\\d+].*\\{.+}.*:"))) {

                val cleanKey = line.substringBefore("[")
                ToonValidator.validateKey(cleanKey, i + 1)
                
                // Check for duplicates at this level
                val levelKeys = keysAtLevel.getOrPut(indent) { mutableSetOf() }
                ToonValidator.checkDuplicateKey(levelKeys, cleanKey, i + 1)
                levelKeys.add(cleanKey)
                
                val schema = line.substringAfter("{").substringBefore("}")
                val headers = schema.split(",").map { it.trim() }
                
                // Validate headers
                if (headers.isEmpty() || headers.any { it.isBlank() }) {
                    throw InvalidToonSyntaxException("Object list schema cannot be empty", i + 1)
                }

                // Consume following indented rows (FIXED: don't use iterator)
                val rows = mutableListOf<List<String>>()
                var j = i + 1
                
                while (j < tokens.size) {
                    val (rowIndent, rowLine) = tokens[j]
                    
                    // Stop if we've moved back to same or lower indent level
                    if (rowIndent <= indent) break
                    
                    // Only process lines with commas (data rows)
                    if ("," in rowLine) {
                        val rowData = rowLine.split(",").map { it.trim() }
                        
                        // Validate row length matches schema
                        if (rowData.size != headers.size) {
                            throw SchemaMismatchException(
                                "Expected ${headers.size} columns but found ${rowData.size}",
                                j + 1
                            )
                        }
                        
                        rows.add(rowData)
                    }
                    
                    j++
                }
                
                // Skip the rows we just consumed
                i = j - 1

                if (stack.isNotEmpty()) {
                    stack.last().add(ToonNode.ObjectListNode(cleanKey, headers, rows))
                }

                lastIndent = indent
                i++
                continue
            }

            // If we reach here, skip the line (might be a data row already consumed)
            i++
        }

        return result
    }
}