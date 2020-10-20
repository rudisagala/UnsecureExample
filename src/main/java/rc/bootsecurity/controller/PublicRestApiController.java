package rc.bootsecurity.controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rc.bootsecurity.db.UserRepository;
import rc.bootsecurity.model.User;

@RestController
@RequestMapping("api/public")
@CrossOrigin 			//the jwt can acces by multiple application
public class PublicRestApiController {
	@Autowired
	private UserRepository userRepository;

    public PublicRestApiController(){}

    @GetMapping("test")
    public String test1(){
        return "API Test";
    }

    @GetMapping("management/reports")
    public String reports(){
        return "some report data";
    }
    @GetMapping("users")
    public List<User> users(){
    	return this.userRepository.findAll();
    }

}
