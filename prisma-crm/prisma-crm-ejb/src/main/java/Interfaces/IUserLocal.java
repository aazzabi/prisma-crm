package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.User;

@Local
public interface IUserLocal {
	public void createUser(User user);
	public boolean activateAccount(String confirmationToken);
	public User findUserById(int id);
	public List<User> findAllUsers();
	public void updateUser(User user);
	public void deleteUser(int id);
	public String loginUser(String username, String pwd);
	public boolean uploadProfileImage(String imgToUpload);
	public boolean changePwd(User user, String oldPwd, String newPwd);
}
