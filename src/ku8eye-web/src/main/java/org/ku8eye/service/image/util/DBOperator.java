package org.ku8eye.service.image.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ku8eye.domain.DockerImage;
import org.ku8eye.service.image.DockerImageImportBean;
import org.ku8eye.service.image.ImageRegistry;
import org.ku8eye.util.SystemUtil;

public class DBOperator {

	private static Logger log = Logger.getLogger(ImageRegistry.class);
	private static String INSERT_DOCKER_IMAGE = "insert into docker_image (TITLE,IMAGE_NAME,VERSION,VERSION_TYPE,PUBLIC_IMAGE,"
			+ "category,CLUSTER_ID,REGISTRY_ID,IMAGE_ICON_URL,STATUS,BUILD_FILE,AUTO_BUILD_COMMAND,AUTO_BUILD,NOTE,"
			+ "LAST_UPDATED,IMAGE_URL,SIZE) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?)";

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

	public static int executeInsertImage(DockerImageImportBean imageShell)
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
			File file = new File(imageShell.getGunzipPath() + File.separator
					+ imageShell.getPath() + File.separator
					+ dockerImage.getBuildFile());
			if (!CommonUtil.isBlank(dockerImage.getBuildFile())
					&& file.exists()) {
				String content = FileUtil.readFile(imageShell.getGunzipPath()
						+ File.separator + imageShell.getPath()
						+ File.separator + dockerImage.getBuildFile());
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
			if (CommonUtil.isBlank(dockerImage.getImageUrl())) {
				pst.setString(15, null);
			} else {
				pst.setString(15, dockerImage.getImageUrl());
			}
			if (dockerImage.getSize() == null) {
				pst.setString(16, null);
			} else {
				pst.setInt(16, dockerImage.getSize());
			}
			return pst.executeUpdate();
		} finally {
			closeStatement(pst);
			closeConnection(conn);
		}
	}

	/**
	 * 用于查询相关数据registryId、url
	 * 
	 * @param publicImage
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static String[] getDBInfo(int clusterId) throws SQLException,
			ClassNotFoundException, IOException {
		String[] resultArray = new String[2];
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String url = "";
		String registryId = "";
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "select id,service_url from ku8s_srv_endpoint where cluster_Id="
					+ clusterId
					+ " and service_type=3 order by service_url limit 1 ";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				registryId = rs.getString("id");
				url = (rs.getString("service_url"));
			}
			url = url.replace("http://", "").replace("https://", "");
			url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
			resultArray[0] = registryId;
			resultArray[1] = url;
			return resultArray;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	/**
	 * 用于查询相关数据registryId、url
	 * 
	 * @param publicImage
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<String> getRegistryUrl(int clusterId)
			throws SQLException, ClassNotFoundException, IOException {
		List<String> list = new ArrayList<String>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String url = "";
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			String sql = "select id,service_url from ku8s_srv_endpoint where cluster_Id="
					+ clusterId
					+ " and service_type=3 order by service_url limit 1 ";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				url = (rs.getString("service_url"));
				// url = url.replace("http://", "").replace("https://", "");
				// url = url.endsWith("/") ? url.substring(0, url.length() - 1)
				// : url;
				list.add(url);
			}
			return list;
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	/**
	 * 查询是否已经存在镜像
	 * 
	 * @param imageShell
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void exitImage(
			List<DockerImageImportBean> dockerImageImportBeanList)
			throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			for (DockerImageImportBean dockerImageImportBean : dockerImageImportBeanList) {
				String sql = "select * from docker_image where image_url='"
						+ dockerImageImportBean.getImage().getImageUrl()
						+ "' and image_name='"
						+ dockerImageImportBean.getImage().getImageName()
						+ "' and version='"
						+ dockerImageImportBean.getImage().getVersion()
						+ "' and cluster_id='"
						+ dockerImageImportBean.getImage().getClusterId() + "'";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					dockerImageImportBean.setExist(true);
				}
			}

		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	public static void getRegistry(DockerImage dkImg) throws SQLException,
			ClassNotFoundException, IOException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			if (dkImg.getPublicImage() == 1) {
				String sql = "select id,cluster_id,service_url from ku8s_srv_endpoint where id=0";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					dkImg.setRegistryId(rs.getInt("id"));
					dkImg.setClusterId(rs.getInt("cluster_id"));
					dkImg.setImageUrl(rs.getString("service_url"));
				}
			} else {
				String sql = "select id,cluster_id,service_url from ku8s_srv_endpoint where id<>0"
						+ " and service_type=3 order by service_url limit 1";
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					String url = rs.getString("service_url");
					url = url.replace("http://", "").replace("https://", "");
					url = url.endsWith("/") ? url
							.substring(0, url.length() - 1) : url;
					dkImg.setRegistryId(rs.getInt("id"));
					dkImg.setClusterId(rs.getInt("cluster_id"));
					dkImg.setImageUrl(url);
				}
			}
		} finally {
			closeResultSet(rs);
			closeStatement(stmt);
			closeConnection(conn);
		}
	}

	public static String addRgistry(int clusterId, String url)
			throws SQLException, ClassNotFoundException, IOException {
		Connection conn = null;
		Statement st = null;
		String sql = "INSERT INTO ku8s_srv_endpoint(NODE_ROLE,SERVICE_TYPE,CLUSTER_ID,HOST_ID,SERVICE_URL,SERVICE_STATUS,NOTE,SSH_PORT,SSH_HOST,SSH_PASS,SSH_USER,LAST_UPDATED) VALUES (null, 3, "
				+ clusterId
				+ ", null, '"
				+ url
				+ "', 1, null, null,null, null,null, null)";
		try {
			conn = getConnection();
			st = conn.createStatement();
			st.execute(sql);
			return "SUCCESS:";
		} finally {
			closeStatement(st);
			closeConnection(conn);
		}
	}

	public static void executeUpdateImage(DockerImageImportBean theImage)
			throws Exception {
		Connection conn = null;
		PreparedStatement pst = null;
		DockerImage dockerImage = theImage.getImage();
		String sql = "update docker_image set TITLE=?,image_name=?,version=?,VERSION_TYPE=?,PUBLIC_IMAGE=?,category=?,cluster_id=?,REGISTRY_ID=?,"
				+ "IMAGE_ICON_URL=?,STATUS=?,BUILD_FILE=?,AUTO_BUILD_COMMAND=?,AUTO_BUILD=?,NOTE=?,LAST_UPDATED=now(),"
				+ "IMAGE_URL=?,SIZE=? where image_url=? and image_name=? and version=? and cluster_id=?";
		try {
			conn = getConnection();
			pst = conn.prepareStatement(sql);
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
			File file = new File(theImage.getGunzipPath() + File.separator
					+ theImage.getPath() + File.separator
					+ dockerImage.getBuildFile());
			if (!CommonUtil.isBlank(dockerImage.getBuildFile())
					&& file.exists()) {
				String content = FileUtil.readFile(theImage.getGunzipPath()
						+ File.separator + theImage.getPath() + File.separator
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
			if (CommonUtil.isBlank(dockerImage.getImageUrl())) {
				pst.setString(15, null);
			} else {
				pst.setString(15, dockerImage.getImageUrl());
			}
			if (dockerImage.getSize() == null) {
				pst.setString(16, null);
			} else {
				pst.setInt(16, dockerImage.getSize());
			}
			if (CommonUtil.isBlank(dockerImage.getImageUrl())) {
				pst.setString(17, null);
			} else {
				pst.setString(17, dockerImage.getImageUrl());
			}
			if (CommonUtil.isBlank(dockerImage.getImageName())) {
				pst.setString(18, null);
			} else {
				pst.setString(18, dockerImage.getImageName());
			}
			if (CommonUtil.isBlank(dockerImage.getVersion())) {
				pst.setString(19, null);
			} else {
				pst.setString(19, dockerImage.getVersion());
			}
			if (dockerImage.getClusterId() != null
					&& !CommonUtil.isBlank(dockerImage.getClusterId()
							.toString())) {
				pst.setInt(20, dockerImage.getClusterId());
			} else {
				pst.setString(20, null);
			}
			pst.executeUpdate();
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			closeStatement(pst);
			closeConnection(conn);
		}

	}

}
