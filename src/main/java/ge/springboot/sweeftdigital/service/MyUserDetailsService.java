package ge.springboot.sweeftdigital.service;

import ge.springboot.sweeftdigital.dao.UserDao;
import ge.springboot.sweeftdigital.entity.MyUserDetails;
import ge.springboot.sweeftdigital.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Qualifier("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
         User user = userDao.findUserByEmail(email);

         if(user == null){
             throw new UsernameNotFoundException("user not found");
         }

         return new MyUserDetails(user);
    }
}
