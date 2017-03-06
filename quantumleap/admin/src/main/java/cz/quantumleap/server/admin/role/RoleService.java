package cz.quantumleap.server.admin.role;

import cz.quantumleap.core.common.NotFoundException;
import cz.quantumleap.core.persistence.transport.SliceRequest;
import cz.quantumleap.core.persistence.transport.Slice;
import cz.quantumleap.core.role.dao.RoleDao;
import cz.quantumleap.core.role.transport.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final RoleDao roleDao;

    public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public Slice findRoles(SliceRequest sliceRequest) {
        return roleDao.fetchSlice(sliceRequest);
    }

    public Role getRole(long id) {
        return roleDao.fetchById(id, Role.class).orElseThrow(() -> new NotFoundException(id));
    }

    @Transactional
    public Role saveRole(Role role) {
        return roleDao.save(role);
    }
}
