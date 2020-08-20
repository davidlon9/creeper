package com.davidlong.http.model.seq.recorder;

import com.davidlong.http.exception.MethodNotSupportException;
import com.davidlong.http.exception.RuntimeExecuteException;

import java.io.IOException;
import java.sql.*;
import java.util.Set;

public class DatabaseUrlRecorder extends AbstractUrlRecorder {
    private DataSource dataSource;
    private String urlColName;
    private String tableName;

    public DatabaseUrlRecorder(DataSource dataSource, String tableName, String urlColName) {
        this.dataSource = dataSource;
        this.urlColName = urlColName;
        this.tableName = tableName;
    }

    @Override
    public void addUrlRecord(String historicalUrl) {
        Connection conn=null;
        PreparedStatement ps=null;
        try {
            String sql="insert into "+tableName+"("+urlColName+") values(?)";
            conn = getConnection();
            ps=conn.prepareStatement(sql);
            ps.setString(1, historicalUrl);
            int res=ps.executeUpdate();
            if(res>0){
                super.addUrlRecord(historicalUrl);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(conn!=null) conn.close();
                if(ps!=null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> readUrlRecords() throws IOException {
        Connection conn=null;
        Statement statement=null;
        ResultSet rs=null;
        try {
            conn = getConnection();
            String sql="select "+ urlColName +" from "+tableName;
            statement = conn.createStatement();
            rs = statement.executeQuery(sql);
            while (rs.next()) {
                addUrlRecord(rs.getString(urlColName));
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(conn!=null) conn.close();
                if(statement!=null) statement.close();
                if(rs!=null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return getUrlRecords();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName(dataSource.getDriverClass());
        } catch (ClassNotFoundException e) {
            throw new RuntimeExecuteException(e);
        }
        return DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
    }

    public void writeUrlRecords() throws IOException {
        throw new MethodNotSupportException();
    }

    public static class DataSource{
        private String driverClass;
        private String username;
        private String password;
        private String url;

        public DataSource(String driverClass, String url, String username, String password) {
            this.driverClass = driverClass;
            this.username = username;
            this.password = password;
            this.url = url;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
