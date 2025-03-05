package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    public User saveUser(User user) {
        if (user.getCompany() != null) {
            Optional<Company> comOptional = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(comOptional.isPresent() ? comOptional.get() : null);
        }
        return this.userRepository.save(user);
    }

    public User updateUser(User userJson) {
        User user = this.fetchUserById(userJson.getId());
        if (user != null) {
            user.setEmail(userJson.getEmail());
            user.setName(userJson.getName());
            user.setPassword(userJson.getPassword());
            user.setAddress(userJson.getAddress());
            user.setAge(userJson.getAge());
            user.setGender(userJson.getGender());
            if (userJson.getCompany() != null) {
                user.setCompany(this.companyRepository.findById(userJson.getCompany().getId()).get());
            }
            user = this.saveUser(user);
        }
        return saveUser(user);
    }

    public void deleteUserById(long id) {
        if (this.userRepository.findById(id) != null) {
            this.userRepository.deleteById(id);
        }
    }

    public User fetchUserById(long id) {
        return this.userRepository.findById(id);
    }

    public ResultPaginationDTO fetchUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);// lúc đầu đã -1
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageUser.getTotalPages());
        meta.setTotal(pageUser.getTotalElements());

        List<ResUserDTO> listResUserDTOs = pageUser.getContent().stream()
                .map(item -> new ResUserDTO(item.getId(), item.getName(), item.getEmail(), item.getAge(),
                        item.getGender(), item.getAddress(), item.getCreatedAt(), item.getUpdatedAt(),
                        new ResUserDTO.CompanyUser(
                                item.getCompany() != null ? item.getCompany().getId() : 0,
                                item.getCompany() != null ? item.getCompany().getName() : null)))
                .collect(Collectors.toList());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setResult(listResUserDTOs);
        result.setMeta(meta);

        return result;
    }

    public User fetchUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public Boolean checkExistedEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
        }
        return new ResCreateUserDTO(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getGender(),
                user.getAddress(), user.getCreatedAt(), company);
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO.CompanyUser company = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
        }
        return new ResUpdateUserDTO(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getGender(),
                user.getAddress(), user.getUpdatedAt(), company);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
        }
        return new ResUserDTO(user.getId(), user.getName(), user.getEmail(), user.getAge(), user.getGender(),
                user.getAddress(), user.getCreatedAt(), user.getUpdatedAt(), company);
    }

    public void updateRefreshToken(String refreshToken, String email) {
        User user = this.userRepository.findByEmail(email);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            this.userRepository.save(user);
        }
    }

    public User fetchUserByRefreshToken(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
