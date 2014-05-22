/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientetcp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author jessica
 */
public class ClienteTCP extends JFrame {

    PrintWriter saida;
    Socket conexao;
    BufferedReader teclado;
    JTextField status, quantidadePecas;
    Scanner entrada;
    static ArrayList<JButton> btnsPecasMesa;
    ArrayList<JButton> btnsPecas;
    ArrayList<String> pecasDisponiveisParaCompra, pecasCompradasNaJogada;
    JButton btnPassarVez, btnComprar;
    Container painelJogador, painelMesa, painelInformacoes, painelBase, painelTopo;
    JRadioButton rbInserirNaEsquerda, rbInserirNaDireita;
    ButtonGroup grupoBotoes;
    JFrame frame;
    int id;

    ClienteTCP(String ip, int porta) throws IOException {
        frame = this;
        configurarLayoutTela();
        criarConexaoComOServidor(ip, porta);
    }

    //"127.0.0.1", 40000
    private void criarConexaoComOServidor(String ip, int porta) throws IOException {
        try {
            conexao = new Socket(ip, porta);//IP servidor, porta servidor
            saida = new PrintWriter(conexao.getOutputStream());
            entrada = new Scanner(conexao.getInputStream());
            aguardarMensagemDoServidor();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao tentar estabelecer conexao com o servidor.\n" + "Erro: " + ex.getMessage());
        }
    }

    private void redesenharPecasDaMesa() {
        painelMesa.removeAll();
        for (JButton btn : btnsPecasMesa) {
            painelMesa.add(BorderLayout.CENTER, btn);
        }
        //força a atualizaçao
        painelMesa.revalidate();
    }

    public void habilitarBotoes() {
        for (JButton b : btnsPecas) {
            b.setEnabled(true);
        }
        btnPassarVez.setEnabled(true);
        btnComprar.setEnabled(true);
    }

    public void desabilitarBotoes() {
        for (JButton b : btnsPecas) {
            b.setEnabled(false);
        }
        btnPassarVez.setEnabled(false);
        btnComprar.setEnabled(false);
    }

    void alterarDesignBotao(final JButton b) {
        b.setSize(200, 500);

        b.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (b.isEnabled() == true) {
                    b.setBackground(Color.cyan);
                    Cursor cursor = Cursor.getDefaultCursor();
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                    setCursor(cursor);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(UIManager.getColor("control"));
                Cursor cursor = Cursor.getDefaultCursor();
                cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
                setCursor(cursor);
            }
        });

    }

    private void configurarLayoutTela() {
        Font fonte = new Font("Calibri", Font.BOLD, 18);
        this.setForeground(Color.WHITE);
        status = new JTextField("Aguardando todos os participantes se conectarem..");
        status.setFont(fonte);
        quantidadePecas = new JTextField();
        quantidadePecas.setFont(fonte);
        status.setBackground(this.getBackground());
        quantidadePecas.setBackground(this.getBackground());
        btnsPecas = new ArrayList();
        btnsPecasMesa = new ArrayList();
        pecasDisponiveisParaCompra = new ArrayList();
        pecasCompradasNaJogada = new ArrayList();
        btnPassarVez = new JButton("Passar Vez");
        alterarDesignBotao(btnPassarVez);
        btnPassarVez.setEnabled(false);
        btnComprar = new JButton("Comprar");
        alterarDesignBotao(btnComprar);
        btnComprar.setEnabled(false);
        grupoBotoes = new ButtonGroup();
        rbInserirNaEsquerda = new JRadioButton("<-");
        rbInserirNaDireita = new JRadioButton("->");
        painelJogador = new JPanel();
        painelMesa = new JPanel();
        painelBase = new JPanel();
        painelTopo = new JPanel();
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.PAGE_AXIS));
        painelInformacoes = new JPanel();
        rbInserirNaEsquerda.setSelected(true);
        grupoBotoes.add(rbInserirNaEsquerda);
        grupoBotoes.add(rbInserirNaDireita);
        painelInformacoes.add(BorderLayout.SOUTH, btnPassarVez);
        painelInformacoes.add(BorderLayout.SOUTH, btnComprar);
        painelJogador.add(rbInserirNaEsquerda);
        painelJogador.add(rbInserirNaDireita);
        for (int i = 0; i < 6; i++) {
            JButton peca = new JButton("");
            peca.setEnabled(false);
            peca.setPreferredSize(new Dimension(55, 40));
            peca.addActionListener(new EnviarMensagemAoServidor());
            alterarDesignBotao(peca);
            btnsPecas.add(peca);
            painelJogador.add(peca);
        }
        painelBase.add(painelInformacoes);
        painelBase.add(painelJogador);
        painelTopo.add(status);
        painelTopo.add(quantidadePecas);
        this.add(BorderLayout.NORTH, painelTopo);
        this.add(BorderLayout.SOUTH, painelBase);
        this.add(BorderLayout.CENTER, painelMesa);

        setVisible(true);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        definirAcoesBotoes();

    }

    private void exibirQuantidadePecasDosJogadores(String quantidadePecasJogadores) {
        quantidadePecas.setText("Qtd de peças: " + quantidadePecasJogadores);
        painelTopo.revalidate();
    }

    private void definirAcoesBotoes() {

        btnPassarVez.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saida.println("#");
                saida.flush();
                desabilitarBotoes();
            }
        });

        btnComprar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (pecasDisponiveisParaCompra.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "N�o existem mais peças dispon�veis para compra.");
                } else {
                    JButton btnPecaComprada = new JButton(pecasDisponiveisParaCompra.get(0));
                    btnPecaComprada.setPreferredSize(new Dimension(55, 40));
                    alterarDesignBotao(btnPecaComprada);
                    btnPecaComprada.addActionListener(new EnviarMensagemAoServidor());
                    btnsPecas.add(btnPecaComprada);
                    pecasCompradasNaJogada.add(btnPecaComprada.getText());
                    painelJogador.add(btnPecaComprada);
                    painelJogador.revalidate();
                    pecasDisponiveisParaCompra.remove(0);

                }
            }
        });

    }

    private void aguardarMensagemDoServidor() {

        String texto;
        while ((texto = entrada.nextLine()) != null) {
            String[] mensagens = texto.split("#");

            if (mensagens[0].equals("0")) {
                this.id = Integer.parseInt(mensagens[1]);
                char equipe = (this.id % 2 == 0) ? 'A' : 'B';
                frame.setTitle("ID:" + this.id + " Equipe:" + equipe);
                String[] pecas = mensagens[2].split(",");
                for (int i = 0; i < btnsPecas.size(); i++) {
                    btnsPecas.get(i).setText(pecas[i]);
                }
                String[] pecasCompra = mensagens[3].split(",");
                for (int i = 0; i < pecasCompra.length; i++) {
                    pecasDisponiveisParaCompra.add(pecasCompra[i]);
                }
            } else if (mensagens[0].equals("1")) {
                int idJogadorDaVez = Integer.parseInt(mensagens[1]);

                if (idJogadorDaVez == this.id) {
                    habilitarBotoes();
                    status.setText("Sua vez!");
                } else {
                    desabilitarBotoes();
                    char equipe = (idJogadorDaVez % 2 == 0) ? 'A' : 'B';
                    status.setText("Aguarde o jogador " + idJogadorDaVez + " - " + " equipe " + equipe + " jogar...");
                }
            } else if (mensagens[0].equals("2")) {
                JButton botaoMesa = new JButton(mensagens[2]);
                botaoMesa.setBackground(Color.black);
                botaoMesa.setForeground(Color.white);
                botaoMesa.setPreferredSize(new Dimension(55, 40));
                botaoMesa.setEnabled(false);
                if (btnsPecasMesa.isEmpty() || "0".equals(mensagens[1])) {
                    btnsPecasMesa.add(0, botaoMesa);
                } else {
                    btnsPecasMesa.add(botaoMesa);
                }
                redesenharPecasDaMesa();
                exibirQuantidadePecasDosJogadores(mensagens[3]);
            }

        }
    }

    public class EnviarMensagemAoServidor implements ActionListener {
//Mensagem Enviada ser� 0#1|2 (esquerda#carta) ou 1#1|2 (direita#carta)

        @Override
        public void actionPerformed(ActionEvent e) {
            char direita = (rbInserirNaDireita.isSelected()) ? '1' : '0';
            JButton pecaClicada = (JButton) e.getSource();
            boolean valido = verificarMovimentoValido(pecaClicada.getText(), direita);
            if (valido) {
                //tem que girar a peça em alguns casos na hora de desenhar
                verificarSeAPecaEstaDoLadoCorreto(pecaClicada, direita);
                //se o jogador s� est� com uma peça, envia tbm as peças das extremidades da mesa
                String pecasCompradas = "";
                for (int i = 0; i < pecasCompradasNaJogada.size(); i++) {
                    pecasCompradas = pecasCompradasNaJogada.get(i) + ",";
                }

                saida.println(direita + "#" + pecaClicada.getText() + "#" + pecasQueEstaoNasPontas() + "#" + pecasCompradas);
                saida.flush();

                pecaClicada.setVisible(false);
                btnsPecas.remove(pecaClicada);
                desabilitarBotoes();
                pecasCompradasNaJogada.clear();
            } else {
                JOptionPane.showMessageDialog(null, "Este movimento n�o � permitido!");
            }
        }

        //valorpeca � algo assim: 1|3 e posicao � '0' se for pra inserir na esquerda e '1' se for pra inserir na direita

        private boolean verificarMovimentoValido(String valorPeca, char posicao) {
            if (btnsPecasMesa.isEmpty()) {
                return true;
            }
            int indiceListaPecasMesa = (posicao == '1') ? (btnsPecasMesa.size() - 1) : 0;

            int indiceLadoPecaMesa = (posicao == '1') ? 2 : 0;
            return btnsPecasMesa.get(indiceListaPecasMesa).getText().split("|")[indiceLadoPecaMesa].contains(valorPeca.split("|")[0])
                    || btnsPecasMesa.get(indiceListaPecasMesa).getText().split("|")[indiceLadoPecaMesa].contains(valorPeca.split("|")[2]);
        }

        //se posicao = direita '1' senao '0'
        //Este m�todo � s� para desenhar do lado correto

        private void verificarSeAPecaEstaDoLadoCorreto(JButton pecaClicada, char posicao) {
            if (btnsPecasMesa.isEmpty()) {
                return;
            }
            String parteEsquerdaPecaClicada = pecaClicada.getText().split("|")[0];
            String parteDireitaPecaClicada = pecaClicada.getText().split("|")[2];
            String parteEsquerdaPecaExtremidadeEsquerda = btnsPecasMesa.get(0).getText().split("|")[0];
            String parteDireitaPecaExtremidadeDireita = btnsPecasMesa.get(btnsPecasMesa.size() - 1).getText().split("|")[2];
            if (posicao == '1') {//se eu estou inserindo na direita eu tenho que olhar o lado direito da peca q ta na mesa
                if (!parteDireitaPecaExtremidadeDireita.contains(parteEsquerdaPecaClicada)) {
                    pecaClicada.setText(parteDireitaPecaClicada + "|" + parteEsquerdaPecaClicada);
                }
            } else {
                if (!parteEsquerdaPecaExtremidadeEsquerda.contains(parteDireitaPecaClicada)) {
                    pecaClicada.setText(parteDireitaPecaClicada + "|" + parteEsquerdaPecaClicada);
                }
            }

        }

        private String pecasQueEstaoNasPontas() {
            if (btnsPecasMesa.isEmpty()) {
                return " ";
            }
            return btnsPecasMesa.get(0).getText() + "," + btnsPecasMesa.get(btnsPecasMesa.size() - 1).getText();
        }
    }

    public static void main(String args[]) throws IOException {
        // String ip = JOptionPane.showInputDialog("Informe o IP do servidor");
        //String porta = JOptionPane.showInputDialog("Informe a Porta do servidor");
        new ClienteTCP("127.0.0.1", 40000);
    }

}
