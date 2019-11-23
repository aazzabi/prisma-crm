package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.User;

@Remote
public interface IUserRemote {
	public int createUser(User user);
	public boolean activateAccount(String confirmationToken);
	public User findUserById(int id);
	public List<User> findAllUsers();
	public void updateUser(User user);
	public void deleteUser(int id);
	public User loginUser(String username, String pwd);
	public boolean uploadProfileImage(String imgToUpload);
	public boolean changePwd(User user, String oldPwd, String newPwd);
	public int AssignAdmin(int id);
	public int AssignClient(int id);
	public int AssignAgents(int id);
}
