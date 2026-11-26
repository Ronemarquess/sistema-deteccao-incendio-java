package br.com.estacio.incendio.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.estacio.incendio.dto.BombeiroRequestDTO;
import br.com.estacio.incendio.dto.LoginRequestDTO;
import br.com.estacio.incendio.model.Bombeiro;
import br.com.estacio.incendio.repository.BombeiroRepository;

@Service
@Transactional
public class BombeiroService {
    private final BombeiroRepository bombeiroRepository;
    private final PasswordEncoder passwordEncoder;

    public BombeiroService(BombeiroRepository bombeiroRepository, PasswordEncoder passwordEncoder) {
        this.bombeiroRepository = bombeiroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Bombeiro cadastrar(BombeiroRequestDTO request) {
        validarDuplicidade(request.email(), request.matricula(), null);

        Bombeiro bombeiro = new Bombeiro();
        aplicarDados(bombeiro, request);
        bombeiro.setAtivo(true);
        return bombeiroRepository.save(bombeiro);
    }

    public Bombeiro autenticar(LoginRequestDTO request) {
        Bombeiro bombeiro = bombeiroRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), bombeiro.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        if (!Boolean.TRUE.equals(bombeiro.getAtivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário desativado");
        }
        return bombeiro;
    }

    @Transactional(readOnly = true)
    public List<Bombeiro> listarTodos() {
        return bombeiroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Bombeiro buscarPorId(Long id) {
        return bombeiroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bombeiro não encontrado"));
    }

    @Transactional(readOnly = true)
    public Bombeiro buscarPorMatricula(String matricula) {
        return bombeiroRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bombeiro não encontrado"));
    }

    public Bombeiro atualizar(Long id, BombeiroRequestDTO request) {
        Bombeiro bombeiro = buscarPorId(id);
        validarDuplicidade(request.email(), request.matricula(), id);
        aplicarDados(bombeiro, request);
        return bombeiroRepository.save(bombeiro);
    }

    public void desativar(Long id) {
        Bombeiro bombeiro = buscarPorId(id);
        bombeiro.setAtivo(false);
        bombeiroRepository.save(bombeiro);
    }

    private void validarDuplicidade(String email, String matricula, Long idIgnorado) {
        boolean emailEmUso = bombeiroRepository.findByEmail(email)
                .filter(bombeiro -> !bombeiro.getId().equals(idIgnorado))
                .isPresent();
        if (emailEmUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        boolean matriculaEmUso = bombeiroRepository.findByMatricula(matricula)
                .filter(bombeiro -> !bombeiro.getId().equals(idIgnorado))
                .isPresent();
        if (matriculaEmUso) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada");
        }
    }

    private void aplicarDados(Bombeiro bombeiro, BombeiroRequestDTO request) {
        bombeiro.setNome(request.nome());
        bombeiro.setEmail(request.email());
        bombeiro.setSenha(passwordEncoder.encode(request.senha()));
        bombeiro.setMatricula(request.matricula());
        bombeiro.setPatente(request.patente());
        bombeiro.setBatalhao(request.batalhao());
        bombeiro.setTelefone(request.telefone());
    }
}
