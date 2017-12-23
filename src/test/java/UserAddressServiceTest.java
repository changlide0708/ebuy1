import com.bdqn.ebuy.pojo.UserAddress;
import com.bdqn.ebuy.service.userAddress.UserAddressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lenovo on 2017/12/23.
 */

public class UserAddressServiceTest {
    @Resource
    private UserAddressService userAddressService;
    @Test
    public void queryAll() throws Exception {
        List<UserAddress> list=userAddressService.queryAll();
        for (UserAddress address : list) {
            System.out.println(address);
        }
    }

}