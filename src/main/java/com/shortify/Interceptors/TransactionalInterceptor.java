package com.shortify.Interceptors;

import java.sql.Connection;

import com.shortify.configs.MySQLConnection;
import com.shortify.utils.ServiceJDBCException;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@TransactionalJdbc
@Interceptor
public class TransactionalInterceptor {

    @Inject
    @MySQLConnection
    private Connection conn;

    @AroundInvoke
    public Object transactional(InvocationContext context) throws Exception{

        if(conn.getAutoCommit()){
            conn.setAutoCommit(false);
        }
        try{
            System.out.println("Iniciando transaccion con la base de datos");
            Object result = context.proceed();
            conn.commit();
            return result;
        }catch(ServiceJDBCException e){
            conn.rollback();
            throw e;
        }
    }
}
