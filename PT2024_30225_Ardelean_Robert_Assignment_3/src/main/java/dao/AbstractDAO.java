package dao;

import connection.ConnectionFactory;
import model.Bill;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T> 
 */
public abstract class AbstractDAO<T> {
	protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
	private final Class<T> type;

	@SuppressWarnings("unchecked")
	public AbstractDAO() {
		this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	private String createSelectQuery(String field) {
		return "SELECT * FROM `" + type.getSimpleName() + "` WHERE " + field + " = ?";
	}

	private String createInsertQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO `").append(type.getSimpleName()).append("` (");
		for (Field field : type.getDeclaredFields()) {
			sb.append(field.getName()).append(",");
		}
		sb.setLength(sb.length() - 1); // Remove last comma
		sb.append(") VALUES (");
		for (Field field : type.getDeclaredFields()) {
			sb.append("?,");
		}
		sb.setLength(sb.length() - 1); // Remove last comma
		sb.append(")");
		return sb.toString();
	}

	private String createUpdateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE `").append(type.getSimpleName()).append("` SET ");
		for (Field field : type.getDeclaredFields()) {
			if (!field.getName().equals(getPrimaryKeyField())) { // Ignoring primary key field
				sb.append(field.getName()).append(" = ?,");
			}
		}
		sb.setLength(sb.length() - 1); // Remove last comma
		sb.append(" WHERE ").append(getPrimaryKeyField()).append(" = ?");
		return sb.toString();
	}

	private String createDeleteQuery() {
		return "DELETE FROM `" + type.getSimpleName() + "` WHERE " + getPrimaryKeyField() + " = ?";
	}

	private String getPrimaryKeyField() {
		return switch (type.getSimpleName()) {
			case "Client" -> "client_id";
			case "Product" -> "product_id";
			case "Order" -> "order_id";
			case "Bill" -> "bill_id";
			default -> throw new IllegalArgumentException("Unsupported type: " + type.getSimpleName());
		};
	}

	public List<T> findAll() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = "SELECT * FROM `" + type.getSimpleName() + "`";
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			return createObjects(resultSet);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public T findById(int id) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String query = createSelectQuery(getPrimaryKeyField());
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setInt(1, id);
			resultSet = statement.executeQuery();
			List<T> result = createObjects(resultSet);
			if (result.isEmpty()) {
				return null;
			}
			return result.get(0);
		} catch (SQLException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
		} finally {
			ConnectionFactory.close(resultSet);
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public T insert(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createInsertQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);
				statement.setObject(i++, field.get(t));
			}
			statement.executeUpdate();
			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				Object key = generatedKeys.getObject(1);
				if (key instanceof BigInteger) {
					key = ((BigInteger) key).intValue();
				}
				if (t instanceof Bill bill) {
					t = (T) new Bill((int) key, bill.order_id(), bill.client_id(), bill.product_id(), bill.quantity(), bill.total_price(), bill.created_at()); // Creează o nouă instanță a record-ului Bill
				} else {
					Field primaryKeyField = type.getDeclaredField(getPrimaryKeyField());
					primaryKeyField.setAccessible(true);
					if (primaryKeyField.getType().equals(int.class)) {
						primaryKeyField.setInt(t, (int) key);
					} else {
						primaryKeyField.set(t, key);
					}
				}
			}
			return t;
		} catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}


	public T update(T t) {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createUpdateQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			int i = 1;
			for (Field field : type.getDeclaredFields()) {
				field.setAccessible(true);
				if (!field.getName().equals(getPrimaryKeyField())) {
					statement.setObject(i++, field.get(t));
				}
			}
			Field primaryKeyField = type.getDeclaredField(getPrimaryKeyField());
			primaryKeyField.setAccessible(true);
			statement.setObject(i, primaryKeyField.get(t));
			statement.executeUpdate();
			return t;
		} catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
		return null;
	}

	public void delete(T t) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		String query = createDeleteQuery();
		try {
			connection = ConnectionFactory.getConnection();
			statement = connection.prepareStatement(query);
			Field primaryKeyField = type.getDeclaredField(getPrimaryKeyField());
			primaryKeyField.setAccessible(true);
			statement.setObject(1, primaryKeyField.get(t));
			statement.executeUpdate();
		} catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
			LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
			if (e instanceof SQLException) {
				throw (SQLException) e;
			}
		} finally {
			ConnectionFactory.close(statement);
			ConnectionFactory.close(connection);
		}
	}

	protected List<T> createObjects(ResultSet resultSet) {
		List<T> list = new ArrayList<>();
		try {
			while (resultSet.next()) {
				if (type.isRecord()) {
					var canonicalConstructor = type.getDeclaredConstructors()[0];
					var parameters = canonicalConstructor.getParameters();
					Object[] args = new Object[parameters.length];

					for (int i = 0; i < parameters.length; i++) {
						var param = parameters[i];
						var value = resultSet.getObject(param.getName());
						args[i] = convertValue(param.getType(), value);
					}

					T instance = (T) canonicalConstructor.newInstance(args);
					list.add(instance);
				} else {
					T instance = type.getDeclaredConstructor().newInstance();
					for (Field field : type.getDeclaredFields()) {
						field.setAccessible(true);
						Object value = resultSet.getObject(field.getName());
						field.set(instance, convertValue(field.getType(), value));
					}
					list.add(instance);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error creating objects from ResultSet", e);
		}
		return list;
	}

	private Object convertValue(Class<?> targetType, Object value) {
		if (value == null) {
			if (targetType.isPrimitive()) {
				if (targetType == int.class) {
					return 0;
				} else if (targetType == double.class) {
					return 0.0;
				} else if (targetType == float.class) {
					return 0.0f;
				} else if (targetType == long.class) {
					return 0L;
				} else if (targetType == boolean.class) {
					return false;
				} else if (targetType == short.class) {
					return (short) 0;
				} else if (targetType == byte.class) {
					return (byte) 0;
				} else if (targetType == char.class) {
					return '\u0000';
				}
			} else {
				return null;
			}
		}

		if (targetType.isAssignableFrom(value.getClass())) {
			return value;
		}
		if (targetType == int.class || targetType == Integer.class) {
			return ((Number) value).intValue();
		}
		if (targetType == double.class || targetType == Double.class) {
			return ((Number) value).doubleValue();
		}
		if (targetType == float.class || targetType == Float.class) {
			return ((Number) value).floatValue();
		}
		if (targetType == long.class || targetType == Long.class) {
			return ((Number) value).longValue();
		}
		if (targetType == boolean.class || targetType == Boolean.class) {
			return value;
		}
		if (targetType == String.class) {
			return value.toString();
		}
		if (targetType == BigDecimal.class) {
			return new BigDecimal(value.toString());
		}
		if (targetType == LocalDateTime.class && value instanceof Timestamp) {
			return ((Timestamp) value).toLocalDateTime();
		}
		throw new IllegalArgumentException("Unsupported type conversion for value: " + value);
	}
}
