package com.bluebelt.fulfillment.service.impl;

import com.bluebelt.fulfillment.exception.*;
import com.bluebelt.fulfillment.model.role.Role;
import com.bluebelt.fulfillment.model.role.RoleName;
import com.bluebelt.fulfillment.model.user.*;
import com.bluebelt.fulfillment.payload.request.InfoRequest;
import com.bluebelt.fulfillment.payload.request.SignUpRequest;
import com.bluebelt.fulfillment.payload.response.ApiResponse;
import com.bluebelt.fulfillment.payload.response.UserIdentityAvailability;
import com.bluebelt.fulfillment.payload.response.UserProfile;
import com.bluebelt.fulfillment.payload.response.UserSummary;
import com.bluebelt.fulfillment.repository.RoleRepository;
import com.bluebelt.fulfillment.repository.UserRepository;
import com.bluebelt.fulfillment.security.UserPrincipal;
import com.bluebelt.fulfillment.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.bluebelt.fulfillment.utils.AppConstants.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userDAO;

    @Autowired
    private RoleRepository roleDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper mapper;

    // password

    @Override
    public UserSummary getCurrentUser(UserPrincipal currentUser) {
        return new UserSummary().builder().id(currentUser.getId())
                .username(currentUser.getUsername()).avatar(currentUser.getAvatar()).build();
    }

    @Override
    public UserIdentityAvailability checkUsernameAvailability(String username) {
        Boolean isAvailable = !userDAO.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    @Override
    public UserIdentityAvailability checkEmailAvailability(String email) {
        Boolean isAvailable = !userDAO.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    @Override
    public UserProfile getUserProfile(String username) {
        User user = userDAO.getUserByName(username);
        UserProfile userProfile = mapper.map(user, UserProfile.class);
        userProfile.setJoinedAt(user.getCreatedAt());
        return userProfile;
    }

    @Override
    public User addUser(SignUpRequest signUpRequest) {
        if(userDAO.existsByUsername(signUpRequest.getUsername())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, USERNAME_IS_ALREADY);
            throw new BadRequestException(apiResponse);
        }

        if(userDAO.existsByEmail(signUpRequest.getEmail())) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, EMAIL_IS_ALREADY);
            throw new BadRequestException(apiResponse);
        }

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null) {
            Role userRole = roleDAO.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException(ROLE_IS_NOT_FOUND));

            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleDAO.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException(ROLE_IS_NOT_FOUND));

                        roles.add(adminRole);

                        break;

                    case "moderator":
                        Role moderatorRole = roleDAO.findByName(RoleName.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException(ROLE_IS_NOT_FOUND));

                        roles.add(moderatorRole);

                        break;

                    default:
                        Role userRole = roleDAO.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException(ROLE_IS_NOT_FOUND));

                        roles.add(userRole);

                        break;
                }
            });
        }

        User user = new User().builder().username(signUpRequest.getUsername()).email(signUpRequest.getEmail())
                .roles(roles).password(passwordEncoder.encode((signUpRequest.getPassword()))).build();

        return userDAO.save(user);
    }

    @Override
    public User updateUser(User newUser, String username, UserPrincipal currentUser) {

        User user = userDAO.getUserByName(username);
        if (user.getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setInfo(newUser.getInfo());

            return userDAO.save(user);

        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update profile of: " + username);
        throw new UnauthorizedException(apiResponse);


    }

    @Override
    public ApiResponse deleteUser(String username, UserPrincipal currentUser) {

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
        if (!user.getId().equals(currentUser.getId()) || !currentUser.getAuthorities()
                .contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {
            ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to delete profile of: " + username);
            throw new AccessDeniedException(apiResponse);
        }

        userDAO.deleteById(user.getId());

        return new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + username);

    }

    @Override
    public ApiResponse updateAvatar(String updateAvatar, UserPrincipal currentUser) {

        User user = userDAO.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, currentUser.getUsername()));
        String avatarUrl = updateAvatar == null ? null : updateAvatar;
        Photo avatar = new Photo(PhotoType.AVATAR, avatarUrl);
        List<Photo> photos = user.getPhotos();
        Optional<Photo> existAvatar = photos.stream().filter(photo -> AVATAR.equals(photo.getType())).findFirst();
        if(existAvatar.isPresent()) {
            photos.remove(existAvatar);
            photos.add(avatar);
        } else {
            photos.add(avatar);
        }
        user.setPhotos(photos);

        return new ApiResponse(Boolean.TRUE, "You update avatar succesfull!");
    }

    @Override
    public ApiResponse giveAdmin(String username) {

        User user = userDAO.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(roleDAO.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new AppException("User role not set")));
        roles.add(
                roleDAO.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userDAO.save(user);
        return new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + username);

    }

    @Override
    public ApiResponse removeAdmin(String username) {

        User user = userDAO.getUserByName(username);
        Set<Role> roles = new HashSet<>();
        roles.add(
                roleDAO.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userDAO.save(user);
        return new ApiResponse(Boolean.TRUE, "You took ADMIN role from user: " + username);

    }

    @Override
    public UserProfile setOrUpdateInfo(UserPrincipal currentUser, InfoRequest infoRequest) {

        User user = userDAO.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, currentUser.getUsername()));

        String nativePlace = infoRequest.getNativePlace();
        String placeOfPermanent = infoRequest.getPlaceOfPermanent();

        Photo identificationCardFront = new Photo(PhotoType.IDENTIFICATION_CARD_FRONT, infoRequest.getIdentificationCardFront());

        Photo identificationCardBack = new Photo(PhotoType.IDENTIFICATION_CARD_BACK, infoRequest.getIdentificationCardBack());

        List<Photo> photos = user.getPhotos();

        photos.add(identificationCardFront);
        photos.add(identificationCardBack);

        Address address = new Address().builder().nativePlace(nativePlace).placeOfPermanent(placeOfPermanent).build();

        String firstName = infoRequest.getFirstName();
        String lastName = infoRequest.getLastName();
        String phone = infoRequest.getPhone();

        String strGender = infoRequest.getGender() == null ? null : infoRequest.getGender().trim();
        Gender gender = null;

        if(strGender != null) {
            if(("male").equalsIgnoreCase(strGender)) {
                gender = Gender.MALE;
            } else if (("female").equalsIgnoreCase(strGender)) {
                gender = Gender.FEMALE;
            } else {
                throw new ResourceNotFoundException(USER, USERNAME, currentUser.getUsername());
            }
        }

        Info info = new Info().builder().firstName(firstName).lastName(lastName)
                .phone(phone).gender(gender).build();

        if (user.getId().equals(currentUser.getId())
                || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))) {

            user.setInfo(info);
            user.setAddress(address);
            user.setPhotos(photos);
            User updatedUser = userDAO.save(user);

            UserProfile userProfile = mapper.map(updatedUser, UserProfile.class);
            userProfile.setJoinedAt(updatedUser.getCreatedAt());

            return userProfile;
        }

        ApiResponse apiResponse = new ApiResponse(Boolean.FALSE, "You don't have permission to update users profile", HttpStatus.FORBIDDEN);
        throw new AccessDeniedException(apiResponse);

    }
}
