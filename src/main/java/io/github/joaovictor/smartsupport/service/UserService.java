package io.github.joaovictor.smartsupport.service;

import io.github.joaovictor.smartsupport.dto.user.UserRequest;
import io.github.joaovictor.smartsupport.dto.user.UserResponse;
import io.github.joaovictor.smartsupport.entity.SupportTeam;
import io.github.joaovictor.smartsupport.entity.User;
import io.github.joaovictor.smartsupport.mapper.UserMapper;
import io.github.joaovictor.smartsupport.repository.SupportTeamRepository;
import io.github.joaovictor.smartsupport.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SupportTeamRepository supportTeamRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
        }
        User user = userMapper.toEntity(request);
        user.setSupportTeam(resolveSupportTeam(request.supportTeamId()));
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return userMapper.toResponse(getUserOrThrow(id));
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        User user = getUserOrThrow(id);
        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário com este e-mail");
        }
        userMapper.updateEntityFromRequest(request, user);
        user.setSupportTeam(resolveSupportTeam(request.supportTeamId()));
        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.delete(getUserOrThrow(id));
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private SupportTeam resolveSupportTeam(UUID supportTeamId) {
        if (supportTeamId == null) {
            return null;
        }
        return supportTeamRepository.findById(supportTeamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipe de suporte não encontrada"));
    }
}
