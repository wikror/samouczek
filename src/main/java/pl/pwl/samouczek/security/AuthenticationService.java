package pl.pwl.samouczek.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.python.google.common.collect.Sets;

import pl.pwl.samouczek.orm.jpa.MaterialEntity;
import pl.pwl.samouczek.orm.jpa.UserEntity;
import pl.pwl.samouczek.persistence.PersistenceServiceProvider;
import pl.pwl.samouczek.persistence.services.MaterialPersistence;
import pl.pwl.samouczek.persistence.services.UserPersistence;

import com.vaadin.server.VaadinSession;

public class AuthenticationService {

	public static final String ROLE_STUDENT = "student";
	public static final String ROLE_ADMIN = "admin";

	public static boolean isAuthenticated() {
		return VaadinSession.getCurrent() != null && VaadinSession.getCurrent().getAttribute("loginInfo") != null;
	}
	
	public static String sha512(String toHash) {	
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("SHA-512 hash not available.");
		}
		
		byte[] digest = md.digest(toHash.getBytes());
		
		//convert the byte to hex format method 2
		StringBuffer hexString = new StringBuffer();
		for (int i=0; i< digest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & digest[i]));
		}
		
		return hexString.toString();
	}
	
	public static void generateRandomHashes(String role) {
		UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
		List<UserEntity> allUsers = userPersistence.retrieveAllByRole(role);
		for (UserEntity user : allUsers) {
			StringBuilder toHash = new StringBuilder();
			toHash.append(user.getUsername());
			toHash.append(user.getEmail());
			toHash.append(user.getFullname());
			toHash.append(System.currentTimeMillis());
			user.setPassword(sha512(toHash.toString()));
			userPersistence.save(user);
		}
	}

	public static boolean sendReminderEmail(String value) {
		UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
		UserEntity user = userPersistence.retrieveByName(value);
		if (user != null) {
			try {
				HtmlEmail email = new HtmlEmail();
				email.setCharset("utf-8");
				email.setHostName("localhost");
				email.setFrom("webmaster@ilintar.org");
				email.setTo(Sets.newHashSet(new InternetAddress(user.getEmail())));
				email.setSubject("Odzyskiwanie hasła do strony Samouczka Python");
				StringBuilder sb = new StringBuilder();
				sb.append("<body>Aby odzyskać hasło do Samouczka, kliknij w poniższy link: </body>\n");
				sb.append("<a href='").append(buildRetrievalLink(user)).append("'>").append(buildRetrievalLink(user)).append("</a><br />");
				email.setHtmlMsg(sb.toString());
				email.send();
				return true;
			} catch (EmailException | AddressException e) {
				e.printStackTrace();
				return false;
			}
			
		} else {
			return false;
		}
		
	}
	
	private static final String serverContext = "http://www.ilintar.org/samouczek";

	private static String buildRetrievalLink(UserEntity user) {
		try {
			StringBuilder uri = new StringBuilder();
			uri.append(serverContext).append("/password/");
			uri.append("?user=").append(URLEncoder.encode(user.getUsername(), "UTF-8"));
			uri.append("&amp;token=").append(user.getPassword());
			return uri.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean verifyHashedPassword(String user, String token) {
		UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
		UserEntity userEntity = userPersistence.retrieveByName(user);
		if (userEntity == null) {
			return false;
		} else {
			return token != null && token.equals(userEntity.getPassword());
		}
	}

	public static boolean changePassword(String user, String value) {
		try {
			UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
			UserEntity userEntity = userPersistence.retrieveByName(user);
			userEntity.setPassword(sha512(value));
			userPersistence.save(userEntity);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean validateLogin(String user, String pass) {
		UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
		UserEntity userEntity = userPersistence.retrieveByName(user);
		if (userEntity == null) {
			return false;
		} else {
			if (verifyHashedPassword(user, sha512(pass))) {
				VaadinSession.getCurrent().setAttribute("loginInfo", userEntity);
				return true;
			} else {
				return false;
			}
		}
	}
	
	public static UserEntity getCurrentLoginInfo() {
		return (UserEntity) VaadinSession.getCurrent().getAttribute("loginInfo");
	}

	public static void logout() {
		VaadinSession.getCurrent().setAttribute("loginInfo", null);	
	}

	public static boolean isAdmin(UserEntity loginInfo) {
		return loginInfo != null && ROLE_ADMIN.equals(loginInfo.getRole());
	}
	
	public static void reloadUserInfo() {
		if (getCurrentLoginInfo() != null) {
			VaadinSession.getCurrent().setAttribute("loginInfo", PersistenceServiceProvider.getService(UserPersistence.class).retrieveByName(getCurrentLoginInfo().getUsername()));
		}
	}

	public static boolean createNewAccount(String username, String fullName, String email) throws AccountCreationException {
		UserPersistence userPersistence = PersistenceServiceProvider.getService(UserPersistence.class);
		UserEntity userEntity = userPersistence.retrieveByName(username);
		if (userEntity != null) {
			throw new AccountCreationException("Użytkownik o tej nazwie już istnieje.");
		}
		userEntity = new UserEntity();
		userEntity.setRole(ROLE_STUDENT);
		userEntity.setEmail(email);
		userEntity.setFullname(fullName);
		userEntity.setUsername(username);
		userEntity.setPassword(UUID.randomUUID().toString());
		try {
			userEntity = userPersistence.save(userEntity);
		} catch (Exception e) {
			throw new AccountCreationException("Błąd przy zapisywaniu użytkownika: " + e.getMessage());
		}
		MaterialPersistence materialPersistence = PersistenceServiceProvider.getService(MaterialPersistence.class);
		MaterialEntity firstMaterial = materialPersistence.load(1);
		Set<MaterialEntity> materials = Sets.newHashSet(firstMaterial);
		userEntity.setMaterials(materials);
		try {
			userEntity = userPersistence.save(userEntity);
		} catch (Exception e) {
			throw new AccountCreationException("Błąd przy dodawaniu pierwszej lekcji: " + e.getMessage());
		}
		return true;
	}
}
