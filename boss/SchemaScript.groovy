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
        AttributeInfoBuilder nmeBuilder = new AttributeInfoBuilder();
        nmeBuilder.setCreateable(true);
        nmeBuilder.setUpdateable(true);
        nmeBuilder.setName(Name.NAME);
        accountAttributes.add(nmeBuilder.build());
        AttributeInfoBuilder uidBuilder = new AttributeInfoBuilder();
        uidBuilder.setCreateable(true);
        uidBuilder.setUpdateable(true);
        uidBuilder.setName(Uid.NAME);
        accountAttributes.add(uidBuilder.build());
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            String colType = meta.getColumnTypeName(i);
            if (!'ID'.equals(colName) && !'LOGIN'.equals(colName)) {
                accountAttributes.add(AttributeInfoBuilder.build(colName, ScriptedSqlUtils.covertOracleTypeToJava(colType)));
            }
        }
    }
}
sql.rows('select * from emp where 1 < 0', getAccountColumns);

Set<AttributeInfo> departmentAttributes = new HashSet<>();
def getDepartmentColumns = { meta ->
    {
        int colCount = meta.getColumnCount()
        AttributeInfoBuilder nmeBuilder = new AttributeInfoBuilder();
        nmeBuilder.setCreateable(true);
        nmeBuilder.setUpdateable(true);
        nmeBuilder.setName(Name.NAME);
        departmentAttributes.add(nmeBuilder.build());
        AttributeInfoBuilder uidBuilder = new AttributeInfoBuilder();
        uidBuilder.setCreateable(true);
        uidBuilder.setUpdateable(true);
        uidBuilder.setName(Uid.NAME);
        departmentAttributes.add(uidBuilder.build());
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            String colType = meta.getColumnTypeName(i);
            if (!'ID'.equals(colName)) {
                departmentAttributes.add(AttributeInfoBuilder.build(colName, ScriptedSqlUtils.covertOracleTypeToJava(colType)));
            }
        }
    }
}
sql.rows('select * from dept where 1 < 0', getDepartmentColumns);

Set<AttributeInfo> appointAttributes = new HashSet<>();
def getAppointColumns = { meta ->
    {
        int colCount = meta.getColumnCount()
        AttributeInfoBuilder nmeBuilder = new AttributeInfoBuilder();
        nmeBuilder.setCreateable(true);
        nmeBuilder.setUpdateable(true);
        nmeBuilder.setName(Name.NAME);
        appointAttributes.add(nmeBuilder.build());
        AttributeInfoBuilder uidBuilder = new AttributeInfoBuilder();
        uidBuilder.setCreateable(true);
        uidBuilder.setUpdateable(true);
        uidBuilder.setName(Uid.NAME);
        appointAttributes.add(uidBuilder.build());
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnName(i).toUpperCase();
            String colType = meta.getColumnTypeName(i);
            if (!'ID'.equals(colName)) {
                appointAttributes.add(AttributeInfoBuilder.build(colName, ScriptedSqlUtils.covertOracleTypeToJava(colType)));

            }
        }
    }
}
sql.rows('select * from appoint where 1 < 0', getAppointColumns);

SchemaBuilder schemaBuilder = new SchemaBuilder(getClass());
schemaBuilder.defineObjectClass("Department", departmentAttributes);
schemaBuilder.defineObjectClass("Appoint", appointAttributes);
schemaBuilder.defineObjectClass(ObjectClass.ACCOUNT_NAME, accountAttributes);
schemaBuilder.build();
