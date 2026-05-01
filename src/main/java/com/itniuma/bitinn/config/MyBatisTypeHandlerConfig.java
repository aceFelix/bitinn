package com.itniuma.bitinn.config;

import com.itniuma.bitinn.enums.UserRole;
import com.itniuma.bitinn.enums.UserStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 枚举类型转换器配置
 * 将 Java 枚举与数据库 VARCHAR 字段互相转换
 */
@Configuration
public class MyBatisTypeHandlerConfig {

    @org.apache.ibatis.type.MappedJdbcTypes(JdbcType.VARCHAR)
    @MappedTypes(UserRole.class)
    public static class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter.getCode());
        }

        @Override
        public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return UserRole.fromCode(rs.getString(columnName));
        }

        @Override
        public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return UserRole.fromCode(rs.getString(columnIndex));
        }

        @Override
        public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return UserRole.fromCode(cs.getString(columnIndex));
        }
    }

    @org.apache.ibatis.type.MappedJdbcTypes(JdbcType.VARCHAR)
    @MappedTypes(UserStatus.class)
    public static class UserStatusTypeHandler extends BaseTypeHandler<UserStatus> {

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, UserStatus parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter.getCode());
        }

        @Override
        public UserStatus getNullableResult(ResultSet rs, String columnName) throws SQLException {
            return UserStatus.fromCode(rs.getString(columnName));
        }

        @Override
        public UserStatus getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            return UserStatus.fromCode(rs.getString(columnIndex));
        }

        @Override
        public UserStatus getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            return UserStatus.fromCode(cs.getString(columnIndex));
        }
    }
}
