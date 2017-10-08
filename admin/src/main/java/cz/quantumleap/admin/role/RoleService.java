package cz.quantumleap.admin.role;

import cz.quantumleap.core.business.ServiceStub;
import cz.quantumleap.core.role.RoleDao;
import cz.quantumleap.core.role.transport.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceStub<Role> {

    public RoleService(RoleDao roleDao) {
        super(Role.class, roleDao);
    }
}
