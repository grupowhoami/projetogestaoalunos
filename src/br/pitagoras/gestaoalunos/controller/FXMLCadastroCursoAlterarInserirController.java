package br.pitagoras.gestaoalunos.controller;

import br.pitagoras.gestaoalunos.common.Utils;
import br.pitagoras.gestaoalunos.dao.CursoDAO;
import br.pitagoras.gestaoalunos.dao.ProfessorDAO;
import br.pitagoras.gestaoalunos.model.Curso;
import br.pitagoras.gestaoalunos.model.Professor;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class FXMLCadastroCursoAlterarInserirController implements Initializable {

    @FXML
    private AnchorPane anchorPaneGeral;

    @FXML
    private AnchorPane anchorPaneConteudo;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnExcluir;

    @FXML
    private Button btnCancelar;

    @FXML
    private Label lblCodigoCadastro;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtDescricao;

    @FXML
    private TextField txtCargaHoraria;

    @FXML
    private ComboBox<Professor> comboBoxProfessor;

    private Curso cursoSelecionado;

    private Professor professor;

    @FXML
    private void handleBtnExcluir(ActionEvent event) {
        excluir();
    }

    @FXML
    private void handleBtnSalvar(ActionEvent event) {
        if (validarCampos()) {
            inserirAlterar();
        }

    }

    @FXML
    private void handleBtnCancelar(ActionEvent event) {
        new Utils.Tela()
                .addCaminhoFXML("/br/pitagoras/gestaoalunos/view/FXMLCadastroCurso.fxml")
                .ehTelaInterna(true)
                .addAnchorPaneTelaInter(anchorPaneGeral)
                .construir();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preecherComboBoxProfessor(null);

    }

    public void inicializarCampos(int id) {
        // Habilitar componentes.
        btnExcluir.setVisible(true);
        txtId.setVisible(true);
        lblCodigoCadastro.setVisible(true);

        // Inicializar campos.
        cursoSelecionado = new CursoDAO().pesquisar(id);
        txtId.setText(String.valueOf(cursoSelecionado.getIdCurso()));
        txtDescricao.setText(cursoSelecionado.getDescCurso());
        txtCargaHoraria.setText(String.valueOf(cursoSelecionado.getCargaHoraria()));
        preecherComboBoxProfessor(cursoSelecionado.getFkIdProfessor());

    }

    // Metódo para buscar os cadastros de professores e inserir no combobox.
    private void preecherComboBoxProfessor(Professor professor) {
        ObservableList<Professor> observableListProfessor;
        List<Professor> listProfessor = new ProfessorDAO().pesquisar();

        observableListProfessor = FXCollections.observableArrayList(listProfessor);
        comboBoxProfessor.getItems().clear();
        // Primeira linha vazia.
        comboBoxProfessor.getItems().add(null);
        comboBoxProfessor.getItems().addAll(observableListProfessor);

        // Se no banco já tiver vinculado ao professor, seleciona o cadastro do mesmo.
        if (professor != null) {
            comboBoxProfessor.getSelectionModel().select(professor);
        }

    }

    private void inserirAlterar() {
        String descricao = txtDescricao.getText();
        Double cargaHoraria = Double.parseDouble(txtCargaHoraria.getText());
        Professor professorFk = comboBoxProfessor.getSelectionModel().getSelectedItem();

        CursoDAO cursoDao = new CursoDAO();

        // Atualizar cadastro.
        if (cursoSelecionado != null) {
            try {
                cursoSelecionado.setDescCurso(descricao);
                cursoSelecionado.setCargaHoraria(cargaHoraria);
                cursoSelecionado.setFkIdProfessor(professorFk);

                if (cursoDao.alterar(cursoSelecionado)) {
                    new Utils.Mensagem()
                            .addMsgConteudo("Registro alterado com sucesso.")
                            .addTipoMsg(Alert.AlertType.INFORMATION)
                            .exibir();

                    new Utils.Tela()
                            .addCaminhoFXML("/br/pitagoras/gestaoalunos/view/FXMLCadastroCurso.fxml")
                            .ehTelaInterna(true)
                            .addAnchorPaneTelaInter(anchorPaneGeral)
                            .construir();

                }
            } catch (Exception e) {
                new Utils.Mensagem()
                        .addMsgCabecalho("Ocorreu um erro ao tentar alterar o registro.")
                        .addMsgConteudo(e.getMessage())
                        .addTipoMsg(Alert.AlertType.INFORMATION)
                        .exibir();

            }
        } // Inserir cadastro.
        else {
            try {
                Curso curso = new Curso();
                curso.setDescCurso(descricao);
                curso.setCargaHoraria(cargaHoraria);
                curso.setFkIdProfessor(professorFk);

                if (cursoDao.inserir(curso)) {
                    new Utils.Mensagem()
                            .addMsgConteudo("Registro inserido com sucesso.")
                            .addTipoMsg(Alert.AlertType.INFORMATION)
                            .exibir();

                    new Utils.Tela()
                            .addCaminhoFXML("/br/pitagoras/gestaoalunos/view/FXMLCadastroCurso.fxml")
                            .ehTelaInterna(true)
                            .addAnchorPaneTelaInter(anchorPaneGeral)
                            .construir();

                }
            } catch (Exception e) {
                new Utils.Mensagem()
                        .addMsgCabecalho("Ocorreu um erro ao tentar inserir o registro.")
                        .addMsgConteudo(e.getMessage())
                        .addTipoMsg(Alert.AlertType.INFORMATION)
                        .exibir();

            }
        }
    }

    private void excluir() {
        ButtonType resposta = new Utils.Mensagem()
                .addTipoMsg(Alert.AlertType.CONFIRMATION)
                .addMsgCabecalho("Deseja excluir o registro?")
                .addMsgConteudo(cursoSelecionado.getDescCurso())
                .exibir().getResult();
        if (resposta == ButtonType.YES) {
            try {
                CursoDAO cursoDao = new CursoDAO();
                if (cursoDao.deletar(cursoSelecionado)) {
                    new Utils.Mensagem()
                            .addMsgConteudo("Registro excluído com sucesso.")
                            .addTipoMsg(Alert.AlertType.INFORMATION)
                            .exibir();

                    new Utils.Tela()
                            .addCaminhoFXML("/br/pitagoras/gestaoalunos/view/FXMLCadastroCurso.fxml")
                            .ehTelaInterna(true)
                            .addAnchorPaneTelaInter(anchorPaneGeral)
                            .construir();
                }
            } catch (Exception e) {
                new Utils.Mensagem()
                        .addMsgCabecalho("Ocorreu um erro ao tentar excluir o registro.")
                        .addMsgConteudo(e.getMessage())
                        .addTipoMsg(Alert.AlertType.INFORMATION)
                        .exibir();
            }
        }

    }

    // Metódo para validar os campos obrigatórios.
    private boolean validarCampos() {
        /* Fonte https://en.it1352.com/article/a45a2908823a47fcaaf9ff3cbb7fdffb.html */
        final PseudoClass errorClass = PseudoClass.getPseudoClass("validacao-erro");
        boolean continua = true;
        final String msg = "Campo obrigatório";

        // Descrição.
        if (txtDescricao.getText().isEmpty()) {
            txtDescricao.pseudoClassStateChanged(errorClass, true);
            txtDescricao.setPromptText(msg);
            continua = false;
        } else {
            txtDescricao.pseudoClassStateChanged(errorClass, false);
            txtDescricao.setPromptText(null);
        }
        //Carga Horária.
        if (txtCargaHoraria.getText().isEmpty()) {
            txtCargaHoraria.pseudoClassStateChanged(errorClass, true);
            txtCargaHoraria.setPromptText(msg);
            continua = false;
        } else {
            txtCargaHoraria.pseudoClassStateChanged(errorClass, false);
            txtCargaHoraria.setPromptText(null);
        }

        return continua;

    }
}
