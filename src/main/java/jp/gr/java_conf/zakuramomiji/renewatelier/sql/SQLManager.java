/*
 * SQLManager.java
 * 
 * Copyright (c) 2018 firiz.
 * 
 * This file is part of Expression program is undefined on line 6, column 40 in Templates/Licenses/license-licence-gplv3.txt..
 * 
 * Expression program is undefined on line 8, column 19 in Templates/Licenses/license-licence-gplv3.txt. is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Expression program is undefined on line 13, column 19 in Templates/Licenses/license-licence-gplv3.txt. is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Expression program is undefined on line 19, column 30 in Templates/Licenses/license-licence-gplv3.txt..  If not, see <http ://www.gnu.org/licenses/>.
 */
package jp.gr.java_conf.zakuramomiji.renewatelier.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;

/**
 *
 * @author firiz
 */
public final class SQLManager {

    private final static SQLManager INSTANCE = new SQLManager();
    private String url = "jdbc:mysql://localhost:3306/atelier";
    private String user = "root";
    private String password = "";
    private Connection conn = null;

    private SQLManager() {
    }

    public static SQLManager getInstance() {
        return INSTANCE;
    }

    public void setup() {
        final AtelierPlugin plugin = AtelierPlugin.getPlugin();
        try (final InputStream inputstream = new FileInputStream(new File(plugin.getDataFolder(), "db.properties"))) {
            final Properties prop = new Properties();
            prop.load(inputstream);
            url = prop.getProperty("url");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<List<Object>> select(final String table, final String column) {
        return select(table, new String[]{column}, null, 1);
    }

    public List<List<Object>> select(final String table, final String column, final Object columnData) {
        return select(table, new String[]{column}, new Object[]{columnData}, 1);
    }

    public List<List<Object>> select(final String table, final String[] columns, final Object[] columnDatas) {
        return select(table, columns, columnDatas, columns.length);
    }

    public List<List<Object>> select(final String table, final String[] columns, final Object[] columnDatas, final int select_size) {
        final List<List<Object>> result = new ArrayList<>();
        try (final Statement stmt = conn.createStatement()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("select ");
            for (int i = 0; i < select_size; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(columns[i]);
            }
            sb.append(" from ").append(table);
            if (columnDatas != null) {
                for (int i = 0; i < columnDatas.length; i++) {
                    if (i > 0) {
                        sb.append(" and ");
                    } else {
                        sb.append(" where ");
                    }
                    sb.append(columns[i]).append("=");
                    addObject(sb, columnDatas[i]);
                }
            }
            final ResultSet resultSet = stmt.executeQuery(sb.toString());
            while (resultSet.next()) {
                final List<Object> dataList = new ArrayList<>();
                for (int i = 0; i < select_size; i++) {
                    dataList.add(resultSet.getObject(columns[i]));
                }
                result.add(dataList);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void insert(final String table, final String column, final Object columnData) {
        insert(table, new String[]{column}, new Object[]{columnData});
    }

    public void insert(final String table, final String[] columns, final Object[] columnDatas) {
        if (columns.length != columnDatas.length) {
            return;
        }
        try (final Statement stmt = conn.createStatement()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("insert into ").append(table).append(" (");
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(columns[i]);
            }
            sb.append(") values (");
            for (int i = 0; i < columnDatas.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                addObject(sb, columnDatas[i]);
            }
            sb.append(") on duplicate key update ");
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(columns[i]).append("=");
                addObject(sb, columnDatas[i]);
            }
            sb.append(";");
            stmt.executeUpdate(sb.toString());
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //なぜ select r.a, s.b, s.c from r,c where r.b = s.bがだめなのか
    public void delete(final String table, final String column, final Object columnData) {
        delete(table, new String[]{column}, new Object[]{columnData});
    }

    public void delete(final String table, final String[] columns, final Object[] columnDatas) {
        if (columns.length != columnDatas.length) {
            return;
        }
        try (final Statement stmt = conn.createStatement()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("delete from ").append(table).append(" where ");
            for (int i = 0; i < columns.length; i++) {
                if (i > 0) {
                    sb.append(" and ");
                }
                sb.append(columns[i]).append("=");
                addObject(sb, columnDatas[i]);
            }
            sb.append(";");
            stmt.executeUpdate(sb.toString());
        } catch (SQLException ex) {
            Logger.getLogger(SQLManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addObject(final StringBuilder sb, final Object obj) {
        if (obj instanceof String) {
            sb.append("'").append(obj).append("'");
        } else {
            sb.append(obj);
        }
    }

}
