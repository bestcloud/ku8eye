package org.ku8eye.service.image.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.image.ImageShell;
import org.ku8eye.util.SystemUtil;

public class DBOperator {

	private static String INSERT_DOCKER_IMAGE = "insert into docker_image (TITLE,IMAGE_NAME,VERSION,VERSION_TYPE,PUBLIC_IMAGE,"
			+ "category,CLUSTER_ID,REGISTRY_ID,IMAGE_ICON_URL,STATUS,BUILD_FILE,AUTO_BUILD_COMMAND,AUTO_BUILD,NOTE,"
			+ "LAST_UPDATED) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())";

	private static Connection getConnection() throws ClassNotFoundException,
			SQLException, IOException {
		Properties props = SystemUtil.getSpringAppProperties();
		Connection conn = null;
		String url = props.getProperty("spring.datasource.url") + "?" + "user="
				+ props.getProperty("spring.datasource.username")
				+ "&password="
				+ props.getProperty("spring.datasource.password")
				+ "&useUnicode=true&characterEncoding=UTF8";
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(url);
		return conn;
	}

	public static int executeInsertImage(String filePath, ImageShell imageShell)
			throws SQLException, ClassNotFoundException, IOException {
		Connection conn = null;
		PreparedStatement pst = null;
		DockerImage dockerImage = imageShell.getImage();
		try {
			conn = getConnection();
			pst = conn.prepareStatement(INSERT_DOCKER_IMAGE);
			if (CommonUtil.isBlank(dockerImage.getTitle())) {
				pst.setString(1, null);
			} else {
				pst.setString(1, dockerImage.getTitle());
			}
			if (CommonUtil.isBlank(dockerImage.getImageName())) {
				pst.setString(2, null);
			} else {
				pst.setString(2, dockerImage.getImageName());
			}
			if (CommonUtil.isBlank(dockerImage.getVersion())) {
				pst.setString(3, null);
			} else {
				pst.setString(3, dockerImage.getVersion());
			}
			if (dockerImage.getVersionType() != null
					&& !CommonUtil.isBlank(dockerImage.getVersionType()
							.toString())) {
				pst.setInt(4, dockerImage.getVersionType());
			} else {
				pst.setString(4, null);
			}
			if (dockerImage.getPublicImage() != null
					&& !CommonUtil.isBlank(dockerImage.getPublicImage()
							.toString())) {
				pst.setInt(5, dockerImage.getPublicImage());
			} else {
				pst.setString(5, null);
			}
			if (CommonUtil.isBlank(dockerImage.getCategory())) {
				pst.setString(6, null);
			} else {
				pst.setString(6, dockerImage.getCategory());
			}
			if (dockerImage.getClusterId() != null
					&& !CommonUtil.isBlank(dockerImage.getClusterId()
							.toString())) {
				pst.setInt(7, dockerImage.getClusterId());
			} else {
				pst.setString(7, null);
			}
			if (dockerImage.getRegistryId() != null
					&& !CommonUtil.isBlank(dockerImage.getRegistryId()
							.toString())) {
				pst.setInt(8, dockerImage.getRegistryId());
			} else {
				pst.setString(8, null);
			}
			if (CommonUtil.isBlank(dockerImage.getImageIconUrl())) {
				pst.setString(9, null);
			} else {
				pst.setString(9, dockerImage.getImageIconUrl());
			}
			if (dockerImage.getStatus() != null
					&& !CommonUtil.isBlank(dockerImage.getStatus().toString())) {
				pst.setInt(10, dockerImage.getStatus());
			} else {
				pst.setString(10, null);
			}
			File file = new File(filePath + File.separator
					+ imageShell.getPath() + File.separator
					+ dockerImage.getBuildFile());
			if (file.exists()) {
				String content = FileUtil.readFile(filePath + File.separator
						+ imageShell.getPath() + File.separator
						+ dockerImage.getBuildFile());
				if (CommonUtil.isBlank(content)) {
					pst.setString(11, null);
				} else {
					pst.setString(11, content);
				}
			} else {
				pst.setString(11, null);
			}

			if (CommonUtil.isBlank(dockerImage.getAutoBuildCommand())) {
				pst.setString(12, null);
			} else {
				pst.setString(12, dockerImage.getAutoBuildCommand());
			}
			if (dockerImage.getAutoBuild() != null
					&& !CommonUtil.isBlank(dockerImage.getAutoBuild()
							.toString())) {
				pst.setInt(13, dockerImage.getAutoBuild());
			} else {
				pst.setString(13, null);
			}
			if (CommonUtil.isBlank(dockerImage.getNote())) {
				pst.setString(14, null);
			} else {
				pst.setString(14, dockerImage.getNote());
			}
			return pst.executeUpdate();
		} finally {
			closeStatement(pst);
			closeConnection(conn);
		}
	}

	public static String getInfo(DockerImage dockerImage) throws SQLException,
			ClassNotFoundException, IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String url = "";
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			if (dockerImage.getClusterId() == 0) {
				String sql = "select id,service_url from ku8s_srv_endpoint where id=0";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					dockerImage.setRegistryId(rs.getInt("id"));
					url = (rs.getString("service_url"));
				}
			} else {
				String sql = "select id,service_url from ku8s_srv_endpoint where cluster_id="
						+ dockerImage.getClusterId()
						+ " and service_type=3 limit 1";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					dockerImage.setRegistryId(rs.getInt("id"));
					url = (rs.getString("service_url"));
				}
			}
			return url;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	public static String executeQuery(String param1, String param2)
			throws SQLException, ClassNotFoundException, IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "select * from docker_image where id=" + param1;
			rs = stmt.executeQuery(sql);
			String a = "";
			while (rs.next()) {
				a = rs.getString(2);
			}
			return a;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	private static void closeResultSet(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
			rs = null;
		}
	}

	private static void closeStatement(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
			stmt = null;
		}
	}

	public static void closeConnection(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

}
