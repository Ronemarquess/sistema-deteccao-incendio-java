package br.com.estacio.incendio.service;

import br.com.estacio.incendio.dto.BombeiroRequest;
import br.com.estacio.incendio.dto.BombeiroResponse;
import br.com.estacio.incendio.exception.AutenticacaoException;
import br.com.estacio.incendio.exception.RegraNegocioException;
import br.com.estacio.incendio.exception.RecursoNaoEncontradoException;
import br.com.estacio.incendio.model.Bombeiro;
import br.com.estacio.incendio.repository.BombeiroRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BombeiroService {

    private final BombeiroRepository repository;
    private final PasswordEncoder passwordEncoder;

    public BombeiroService(BombeiroRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public BombeiroResponse cadastrar(BombeiroRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new RegraNegocioException("Já existe bombeiro com este e-mail");
        }
        Bombeiro bombeiro = new Bombeiro();
        aplicarDados(bombeiro, request);
        bombeiro.setSenha(passwordEncoder.encode(request.senha()));
        return BombeiroResponse.de(repository.save(bombeiro));
    }

    public BombeiroResponse login(String email, String senha) {
        Bombeiro bombeiro = repository.findByEmail(email)
                .orElseThrow(() -> new AutenticacaoException("E-mail ou senha inválidos"));
        if (!Boolean.TRUE.equals(bombeiro.getAtivo())
                || !passwordEncoder.matches(senha, bombeiro.getSenha())) {
            throw new AutenticacaoException("E-mail ou senha inválidos");
        }
        return BombeiroResponse.de(bombeiro);
    }

    @Transactional(readOnly = true)
    public List<BombeiroResponse> listar() {
        return repository.findAll().stream().map(BombeiroResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public Bombeiro buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bombeiro não encontrado: " + id));
    }

    public BombeiroResponse atualizar(Long id, BombeiroRequest request) {
        Bombeiro bombeiro = buscarEntidade(id);
        aplicarDados(bombeiro, request);
        if (request.senha() != null && !request.senha().isBlank()) {
            bombeiro.setSenha(passwordEncoder.encode(request.senha()));
        }
        return BombeiroResponse.de(repository.save(bombeiro));
    }

    public BombeiroResponse definirAtivo(Long id, Boolean ativo) {
        Bombeiro bombeiro = buscarEntidade(id);
        bombeiro.setAtivo(ativo);
        return BombeiroResponse.de(repository.save(bombeiro));
    }

    private void aplicarDados(Bombeiro bombeiro, BombeiroRequest request) {
        bombeiro.setNome(request.nome());
        bombeiro.setEmail(request.email());
        bombeiro.setMatricula(request.matricula());
        bombeiro.setPatente(request.patente());
        bombeiro.setBatalhao(request.batalhao());
        bombeiro.setTelefone(request.telefone());
    }
}
