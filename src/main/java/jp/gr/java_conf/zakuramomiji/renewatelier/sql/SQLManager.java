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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import jp.gr.java_conf.zakuramomiji.renewatelier.AtelierPlugin;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SelectValue.BetweenValue;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SelectValue.ConditionType;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SelectValue.InValue;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SelectValue.KeyValue;
import jp.gr.java_conf.zakuramomiji.renewatelier.sql.SelectValue.Pipe;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.Chore;
import jp.gr.java_conf.zakuramomiji.renewatelier.utils.DoubleData;

/**
 *
 * @author firiz
 */
public enum SQLManager {
    INSTANCE; // enum singleton style

    private String url = "jdbc:mysql://localhost:3306/atelier";
    private String user = "root";
    private String password = "";
    private Connection conn = null;

    /**
     * Setup SQLManager
     *
     * <p>
     * カレントディレクトリにdb.propertiesファイルが存在する場合は そのファイルを読み込み、存在しない場合は下記の設定でSQLに接続します。
     * </p>
     *
     * <ul>
     * <li>{@code url=jdbc:mysql://localhost:3306/atelier}</li>
     * <li>{@code user=root}</li>
     * <li>{@code password=}</li>
     * </ul>
     *
     * @since 2018-12-10 / firiz
     *
     */
    public void setup() {
        final AtelierPlugin plugin = AtelierPlugin.getPlugin();
        try (final InputStream inputstream = new FileInputStream(new File(plugin.getDataFolder(), "db.properties"))) {
            final Properties prop = new Properties();
            prop.load(inputstream);
            url = prop.getProperty("url");
            user = prop.getProperty("user");
            password = prop.getProperty("password");
        } catch (FileNotFoundException ex) {
            //Chore.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Chore.log(Level.SEVERE, null, ex);
        }
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            Chore.log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    /**
     * Close SQLConnection
     *
     * <p>
     * SQLのコネクションを閉じます。
     * </p>
     *
     * @since 2018-12-10 / firiz
     *
     */
    public void close() {
        try {
            conn.close();
        } catch (SQLException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Execute select query
     *
     * <p>
     * {@code table}の{@code column}をリストアップし返します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code select("mail", "name")}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th></tr>
     * <tr><td>John</td></tr>
     * <tr><td>Mike</td></tr>
     * <tr><td>Steve</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param column String {@code table}から参照するカラム名
     * @return List&lt;List&lt;Object&gt;&gt; 列&lt;行&lt;値&gt;&gt;
     */
    public List<List<Object>> select(final String table, final String column) {
        return select(table, new String[]{column}, null, 1);
    }

    /**
     * Execute select query
     *
     * <p>
     * {@code table}の{@code column}に対して{@code columnData}の値が 一致するものをリストアップし返します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code select("mail", "name", "John")}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th></tr>
     * <tr><td>John</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param column String {@code table}から参照するカラム名
     * @param columnData Object {@code column}に対しての値
     * @return List&lt;List&lt;Object&gt;&gt; 列&lt;行&lt;値&gt;&gt;
     */
    public List<List<Object>> select(final String table, final String column, final Object columnData) {
        return select(table, new String[]{column}, new Object[]{columnData}, 1);
    }

    /**
     * Execute select query
     *
     * <p>
     * {@code table}の{@code columns}に対して{@code columnDatas}の値が
     * 一致するものをリストアップし返します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code select("mail", new String[]("name", "email"), new Object[]("John"))}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th><th>email</th></tr>
     * <tr><td>John</td><td>john@example.com</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param columns String[] {@code table}から参照するカラム名配列
     * @param columnDatas Object[] {@code columns}に対しての値
     * @return List&lt;List&lt;Object&gt;&gt; 列&lt;行&lt;値&gt;&gt;
     */
    public List<List<Object>> select(final String table, final String[] columns, final Object[] columnDatas) {
        return select(table, columns, columnDatas, columns.length);
    }

    /**
     * Execute select query
     *
     * <p>
     * {@code table}の{@code columns}に対して{@code columnDatas}の値が
     * 一致するものをリストアップし返します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code select("mail", new String[]("name", "email"), new Object[]("John"))}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th><th>email</th></tr>
     * <tr><td>John</td><td>john@example.com</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param columns String[] {@code table}から参照するカラム名配列
     * @param columnDatas Object[] {@code columns}に対しての値
     * @param select_size int 選択範囲
     * @return List&lt;List&lt;Object&gt;&gt; 列&lt;行&lt;値&gt;&gt;
     */
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
            Chore.log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public List<List<Object>> select(final SelectValue value) {
        final List<List<Object>> result = new ArrayList<>();
        try (final Statement stmt = conn.createStatement()) {
            final StringBuilder sbmain = new StringBuilder();
            sbmain.append("select ");

            final StringBuilder sbkeys = new StringBuilder();
            final StringBuilder sbvals = new StringBuilder();
            final LinkedHashMap<KeyValue, DoubleData<ConditionType, Pipe>> vals = value.getVals();
            int i = 0;
            for (final KeyValue key : vals.keySet()) {
                sbkeys.append(",").append(key.getColumn());

                if (key.getValue() != null) {
                    final DoubleData<ConditionType, Pipe> val = vals.get(key);
                    if (val != null) {
                        sbvals.append(key.getColumn()).append(" ");

                        switch (val.getLeft()) {
                            case BETWEEN:
                            case NOT_BETWEEN:
                                if (key instanceof BetweenValue) {
                                    sbvals.append(val.getLeft()).append(" ").append(key.getValue());
                                    break;
                                }
                                throw new IllegalArgumentException(ConditionType.BETWEEN + " needs " + BetweenValue.class);
                            case LIKE:
                                String str = key.getValue();
                                if (!(str.startsWith("'") && str.endsWith("'"))) {
                                    str = "'" + str + "'";
                                }
                                sbvals.append(val.getLeft()).append(" ").append(str);
                                break;
                            case IN:
                                if (key instanceof InValue) {
                                    sbvals.append(val.getLeft()).append("(").append(key.getValue()).append(")");
                                    break;
                                }
                                throw new IllegalArgumentException(ConditionType.IN + " needs " + InValue.class);
                            default:
                                sbvals.append(val.getLeft()).append(" ").append(key.getValue());
                                break;
                        }

                        final Pipe pipe = val.getRight();
                        if (pipe != null && pipe != Pipe.NONE) {
                            sbvals.append(" ").append(pipe).append(" ");
                        }
                    }
                }
                i++;
            }
            sbmain.append(sbkeys.substring(1))
                    .append(" from ")
                    .append(value.getTable())
                    .append(sbvals.length() == 0 ? "" : " where ".concat(sbvals.toString()))
                    .append(value.getOrderBys().isEmpty() ? "" : " order by ".concat(value.getOrderBys().toString()))
                    .append(value.getOrderType() == null ? "" : " ".concat(value.getOrderType().toString()))
                    .append(";");
            final ResultSet resultSet = stmt.executeQuery(sbmain.toString());
            while (resultSet.next()) {
                final List<Object> dataList = new ArrayList<>();
                for (final KeyValue key : vals.keySet()) {
                    dataList.add(resultSet.getObject(key.getColumn()));
                }
                result.add(dataList);
            }
        } catch (SQLException ex) {
            Chore.log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Execute insert ... on duplicate key update query.
     *
     * <p>
     * {@code table}の{@code column}に対して{@code columnData}の値を
     * 存在しない場合はinsertで追加し、存在する場合はdeplicate key updateで更新します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code insert("mail", "name", "John")}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th><th>email</th></tr>
     * <tr><td>John</td><td>null</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table 参照するテーブル名
     * @param column {@code table}から参照するカラム名
     * @param columnData {@code column}に対しての値
     */
    public void insert(final String table, final String column, final Object columnData) {
        insert(table, new String[]{column}, new Object[]{columnData});
    }

    /**
     * Execute insert ... on duplicate key update query.
     *
     * <p>
     * {@code table}の{@code columns}に対して{@code columnDatas}の値を
     * 存在しない場合はinsertで追加し、存在する場合はdeplicate key updateで更新します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code insert("mail", new String[]{"name", "email"}, new Object[]{"John", "john@example.com"})}</pre></blockquote>
     *
     * <p>
     * <strong>example - result</strong></p>
     * <blockquote><table>
     * <tr><th>name</th><th>email</th></tr>
     * <tr><td>John</td><td>john@example.com</td></tr>
     * </table></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param columns String[] {@code table}から参照するカラム名の配列
     * @param columnDatas Object[] {@code columns}に対しての値の配列
     */
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
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Execute delete query
     *
     * <p>
     * {@code table}の{@code column}に対して{@code columnData}の値が 一致するものを削除します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code delete("mail", "name", "John")}</pre></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param column String {@code table}から参照するカラム名
     * @param columnData Object {@code column}に対しての値
     */
    public void delete(final String table, final String column, final Object columnData) {
        delete(table, new String[]{column}, new Object[]{columnData});
    }

    /**
     * Execute delete query
     *
     * <p>
     * {@code table}の{@code columns}に対して{@code columnDatas}の値が 一致するものを削除します。
     * </p>
     *
     * <p>
     * <strong>example - code</strong></p>
     * <blockquote><pre>{@code delete("mail", new String[]("name"), new Object[]("John"))}</pre></blockquote>
     *
     * @since 2018-12-10 / firiz
     * @param table String 参照するテーブル名
     * @param columns String[] {@code table}から参照するカラム名の配列
     * @param columnDatas Object[] {@code columns}に対しての値の配列
     */
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
            Chore.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Convert if Object is a String, add it to StringBuilder without converting
     * otherwise.
     *
     * <p>
     * ObjectがStringの場合は変換し、 それ以外の場合はStringBuilderに変換せずに追加します。
     * </p>
     *
     * @since 2018-12-10 / firiz
     * @param sb
     * @param obj
     */
    private void addObject(final StringBuilder sb, final Object obj) {
        if (obj instanceof String) {
            sb.append("'").append(
                    ((String) obj).replace("'", "\"").replace("\\", "/")
            ).append("'");
        } else {
            sb.append(obj);
        }
    }

}
