package com.nutrify.lib
data class FieldQuery(val name: String, val alias: String)


//TODO: COME BACK TO SQL BUILDER LATER
data class Table(var name: String, var alias: String, var querySubstring: String)

data class Field(var name: String, var alias: String, var type: String?)
data class Fields(var fields: List<Field>, var querySubstring: String)
data class GroupBy(var fields: List<Field>, var querySubstring: String)
data class OrderBy(var fields: List<Field>, var direction: String, var querySubstring: String)
data class Offset(var offset: Int, var querySubstring: String)
data class Limit(var limit: Int, var querySubstring: String)
data class Where(var field: String, var operator: String, var value: String)
data class WhereConditions(var conditions: List<Where>, var querySubstring: String)


class SQLBuilder() {
    var sqlQuery: String? =  null
    var fields: Fields? = null
    var table: Table? = null
    var clauses: WhereConditions? = null
    var groupBy: GroupBy? = null
    var orderBy: OrderBy? = null
    var limit: Limit? = null
    var offset: Offset? = null


    fun select(fields: List<FieldQuery>): SQLBuilder {
       val queryFields =  fields.map { Field(it.name, it.alias, it.alias) }
        val querySubstring = "SELECT ${queryFields.joinToString(", ")} "
        val fields = Fields(queryFields, querySubstring)
        this.sqlQuery = querySubstring
        this.fields = fields
        return this
    }

    fun from(table: String, alias: String): SQLBuilder {
        val substring = "FROM $table "
        val querySubstring = sqlQuery + substring
        this.table = Table(table, alias, querySubstring)
        this.sqlQuery = querySubstring
        return this
    }

    fun where(field: String, operator: String, value: String): SQLBuilder {
        var querySubstring = ""
        if(clauses == null)  {
        querySubstring = "WHERE $field $operator '$value' "
        } else {
            querySubstring = "$field $operator '$value' "
        }
        val where = Where(field, operator, value)
        this.clauses
        return this
    }

    fun and(): SQLBuilder {
        return this
    }
    fun or(): SQLBuilder {
        return this
    }

    fun orderBy(fields: List<String>): SQLBuilder {
        return this
    }

    fun groupBy(fields: List<String>):SQLBuilder {
        return this
    }

    fun limit(limit: Int): SQLBuilder {
        return this
    }

    fun offset(offset: Int): SQLBuilder {
        return this
    }
}