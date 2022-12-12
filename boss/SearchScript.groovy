import groovy.sql.Sql
import org.forgerock.openicf.connectors.scriptedsql.ScriptedSQLConfiguration
import org.forgerock.openicf.misc.scriptedcommon.ICFObjectBuilder
import org.forgerock.openicf.misc.scriptedcommon.OperationType
import org.identityconnectors.common.logging.Log
import org.identityconnectors.framework.common.exceptions.ConnectorException
import org.identityconnectors.framework.common.objects.ConnectorObject
import org.identityconnectors.framework.common.objects.ObjectClass
import org.identityconnectors.framework.common.objects.OperationOptions
import org.identityconnectors.framework.common.objects.ResultsHandler
import org.identityconnectors.framework.common.objects.SearchResult
import org.identityconnectors.framework.common.objects.filter.Filter

import java.sql.Connection

import common.ScriptedSqlUtils
import common.ColumnPrefixMapper

def log = log as Log
def operation = operation as OperationType
def options = options as OperationOptions
def objectClass = objectClass as ObjectClass
def configuration = configuration as ScriptedSQLConfiguration
def filter = filter as Filter
def connection = connection as Connection
def query = query as Closure
def handler = handler as ResultsHandler

log.info("Entering " + operation + " Script")

def sql = new Sql(connection)

ObjectClass departmentObjectClass = new ObjectClass("Department")
ObjectClass appointObjectClass = new ObjectClass("Appoint")

switch (objectClass) {
    case ObjectClass.ACCOUNT:
        handleAccount(sql)
        break
    case departmentObjectClass:
        handleDepartment(sql)
        break
    case appointObjectClass:
        handleAppoint(sql)
        break
    default:
        throw new ConnectorException("Unknown object class " + objectClass)
}

return new SearchResult()

void handleDepartment(Sql sql) {
    Closure closure = { row ->
        buildDepartment(sql, row)
    }
    def sqlQuery = '''select 
	ID,
	NAME,
	D_FROM,
	D_TO,
	DEPT_ID,
	DEPT_LEVEL,
	NAME_ENG,
	AHEAD
	from dept
	where D_FROM <= SYSDATE and SYSDATE <= D_TO'''

    Map params = [:]
    String uidColumn = 'ID';
    String nameColumn = 'NAME';


    String where = ScriptedSqlUtils.buildWhereClause(filter, params, uidColumn, nameColumn, new ColumnPrefixMapper(''), Integer.class)

    if (!where.isEmpty()) {
        sqlQuery += " and " + where
    }

    sql.withTransaction {
        ScriptedSqlUtils.executeQuery(sqlQuery, params, options, closure, handler, sql)
    }
}

void handleAppoint(Sql sql) {
    Closure closure = { row ->
        buildAppoint(sql, row)
    }
    def sqlQuery = '''select 
	ID,
	NAME,
	NAME_ENG,
	from appoint'''

    Map params = [:]
    String uidColumn = 'ID';
    String nameColumn = 'NAME';

    String where = ScriptedSqlUtils.buildWhereClause(filter, params, uidColumn, nameColumn, new ColumnPrefixMapper(''), Integer.class)

    if (!where.isEmpty()) {
        sqlQuery += " where " + where
    }

    sql.withTransaction {
        ScriptedSqlUtils.executeQuery(sqlQuery, params, options, closure, handler, sql)
    }
}

void handleAccount(Sql sql) {
    Closure closure = { row ->
        buildAccount(sql, row)
    }
    def sqlQuery = '''select 
    STATUS_EMP,
    ID,           
    D_IN,
    D_OUT,
    MATERNITY_FROM,
    MATERNITY_TO,
    AS TRIAL_PERIOD,
    D_BIRTH,         
    TAB_N,                  
    CARD_ID,               
    L_NAME,               
    F_NAME,              
    M_NAME,           
    NAME_OLD,
    APPOINT_ID,
    APPOINT_NAME,         
    APPOINT_ENAME,
    SEX, 
    COST_CENTER, 
    EMAIL,
    DEPT_ID,
    BOSS1,
    BOSS2,
    BOSS3,
    EMPSTAT_ID,
    LOGIN,
    LOC, 
    ROOM,
    EXT,
    EMAIL_EXT,
    MATERNITY,
    LEAVE_PLAN,
    BOARD,
    BOARD1,
    BOARD2,
    PHOTO,
    SNILS,
    FHEAD,
    COMPANY_ID,
    TECH_DISSM,
    BOSS_MOBILE
    from emp'''

    Map params = [:]
    String uidColumn = 'ID';
    String nameColumn = 'LOGIN';

    String where = ScriptedSqlUtils.buildWhereClause(filter, params, uidColumn, nameColumn, new ColumnPrefixMapper(''), String.class)

    if (!where.isEmpty()) {
        sqlQuery += " where " + where
    }

    sql.withTransaction {
        ScriptedSqlUtils.executeQuery(sqlQuery, params, options, closure, handler, sql)
    }
}

static ConnectorObject buildDepartment(Sql sql, GroovyObject row) {
    return ICFObjectBuilder.co {
        objectClass 'CustomDepartmentObjectClass'
        uid row.ID as String
        id row.ID as String
        attribute 'NAME', row.NAME
        attribute 'D_FROM', row.D_FROM
        attribute 'D_TO', row.D_TO
        attribute 'DEPT_ID', row.DEPT_ID
        attribute 'DEPT_LEVEL', row.DEPT_LEVEL
        attribute 'NAME_ENG', row.NAME_ENG
        attribute 'AHEAD', row.AHEAD
    }
}

static ConnectorObject buildAppoint(Sql sql, GroovyObject row) {
    return ICFObjectBuilder.co {
        objectClass 'CustomAppointObjectClass'
        uid row.ID as String
        id row.ID as String
        attribute 'NAME', row.NAME
        attribute 'NAME_ENG', row.NAME_ENG
    }
}

static ConnectorObject buildAccount(Sql sql, GroovyObject row) {
    return ICFObjectBuilder.co {
        objectClass ObjectClass.ACCOUNT
        uid row.ID as String
        id row.LOGIN
        attribute 'STATUS_EMP', row.STATUS_EMP
        attribute 'TAB_N', row.TAB_N
        attribute 'CARD_ID', row.CARD_ID
        attribute 'L_NAME', row.L_NAME
        attribute 'F_NAME', row.F_NAME
        attribute 'M_NAME', row.M_NAME
        attribute 'NAME_OLD', row.NAME_OLD
        attribute 'APPOINT_ID', row.APPOINT_ID
        attribute 'APPOINT_NAME', row.APPOINT_NAME
        attribute 'APPOINT_ENAME', row.APPOINT_ENAME
        attribute 'SEX', row.SEX
        attribute 'COST_CENTER', row.COST_CENTER
        attribute 'EMAIL', row.EMAIL
        attribute 'D_IN', row.D_IN
        attribute 'D_OUT', row.D_OUT
        attribute 'DEPT_ID', row.DEPT_ID
        attribute 'BOSS1', row.BOSS1
        attribute 'BOSS2', row.BOSS2
        attribute 'BOSS3', row.BOSS3
        attribute 'EMPSTAT_ID', row.EMPSTAT_ID
        attribute 'LOC', row.LOC
        attribute 'ROOM', row.ROOM
        attribute 'EXT', row.EXT
        attribute 'EMAIL_EXT', row.EMAIL_EXT
        attribute 'MATERNITY', row.MATERNITY
        attribute 'MATERNITY_FROM', row.MATERNITY_FROM
        attribute 'MATERNITY_TO', row.MATERNITY_TO
        attribute 'LEAVE_PLAN', row.LEAVE_PLAN
        attribute 'BOARD', row.BOARD
        attribute 'BOARD1', row.BOARD1
        attribute 'BOARD2', row.BOARD2
        attribute 'TRIAL_PERIOD', row.TRIAL_PERIOD
        attribute 'PHOTO', row.PHOTO
        attribute 'D_BIRTH', row.D_BIRTH
        attribute 'SNILS', row.SNILS
        attribute 'FHEAD', row.FHEAD
        attribute 'COMPANY_ID', row.COMPANY_ID
        attribute 'TECH_DISSM', row.TECH_DISSMd
        attribute 'BOSS_MOBILE', row.BOSS_MOBILE
    }
}
