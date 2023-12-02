package rage.pitclient.login;

import java.net.Proxy;

import org.apache.logging.log4j.LogManager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

/**
 * Basic implementation of Mojang's 'Yggdrasil' login system, purely intended as a dev time bare bones login.
 * Login errors are not handled.
 */
public class Yggdrasil
{
    public static String[] login(String user, String pass)
    {
        if (user == null || pass == null || user.length() == 0 || pass.length() == 0) {
        	String[] ret = { "Empty Field" };
        	return ret;
        }
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(user);
        auth.setPassword(pass);

        try
        {
            auth.logIn();
        }
        catch (AuthenticationException e)
        {
            LogManager.getLogger("FMLTWEAK").error("-- Login failed!  " + e.getMessage());
            String[] ret = { e.getMessage() };
            return ret; // dont set other variables
        }

        String[] ret = {
        	auth.getSelectedProfile().getName(), 
        	auth.getSelectedProfile().getId().toString().replace("-", ""),
        	auth.getAuthenticatedToken(),
        	auth.getUserProperties().toString()};
        
        return ret;
    }
}