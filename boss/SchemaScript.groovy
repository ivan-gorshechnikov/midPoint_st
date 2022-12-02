import common.ScriptedSqlUtils
import org.forgerock.openicf.misc.scriptedcommon.OperationType
import org.identityconnectors.common.logging.Log
import org.identityconnectors.framework.common.objects.ObjectClass
import org.identityconnectors.framework.common.objects.AttributeInfo
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder

import java.sql.*

import groovy.sql.Sql

def log = log as Log
def operation = operation as OperationType
def connection = connection as Connection
def sql = new Sql(connection)

log.info("Entering " + operation + " Script")
Set<AttributeInfo> accountAttributes = new HashSet<>();

def getAccountColumns = { meta ->
    {
        int colCount = meta.getColumnCount()
        ScriptedSqlUtils.addUidAndName(accountAttributes);
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            Class<?> colType = ScriptedSqlUtils.covertOracleTypeToJava(meta.getColumnTypeName(i));
            if (!'ID'.equals(colName) && !'LOGIN'.equals(colName)) {
                accountAttributes.add(AttributeInfoBuilder.build(colName, colType));
            }
        }
    }
}

Set<AttributeInfo> departmentAttributes = new HashSet<>();
def getDepartmentColumns = { meta ->
    {
        int colCount = meta.getColumnCount()
        ScriptedSqlUtils.addUidAndName(departmentAttributes)
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            Class<?> colType = ScriptedSqlUtils.covertOracleTypeToJava(meta.getColumnTypeName(i));
            departmentAttributes.add(AttributeInfoBuilder.build(colName, colType));
        }
    }
}

Set<AttributeInfo> appointAttributes = new HashSet<>();
def getAppointColumns = { meta ->
    {
        int colCount = meta.getColumnCount()
        ScriptedSqlUtils.addUidAndName(appointAttributes)
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            Class<?> colType = ScriptedSqlUtils.covertOracleTypeToJava(meta.getColumnTypeName(i));
            appointAttributes.add(AttributeInfoBuilder.build(colName, colType));
        }
    }
}
sql.rows('select * from emp where 1 < 0', getAccountColumns);
sql.rows('select * from dept where 1 < 0', getDepartmentColumns);
sql.rows('select * from appoint where 1 < 0', getAppointColumns);

SchemaBuilder schemaBuilder = new SchemaBuilder(getClass());
schemaBuilder.defineObjectClass("Department", departmentAttributes);
schemaBuilder.defineObjectClass("Appoint", appointAttributes);
schemaBuilder.defineObjectClass(ObjectClass.ACCOUNT_NAME, accountAttributes);
schemaBuilder.build();
