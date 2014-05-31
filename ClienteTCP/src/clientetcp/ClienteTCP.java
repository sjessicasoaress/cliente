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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

/**
 *
 * @author jessica
 */
public class ClienteTCP extends JFrame {

    PrintWriter saida;
    Socket conexao;
    JTextField txtStatus, txtQuantidadePecas;
    Scanner entrada;
    ArrayList<JButton> btnsPecasMesa, btnsPecas;
    ArrayList<String> pecasDisponiveisParaCompra, pecasCompradasNaJogada;
    JButton btnPassarVez, btnComprar;
    Container painelJogador, painelMesa, painelAcoes, painelBase, painelTopo;
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
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }
    }

    private void redesenharPecasDaMesa() {
        painelMesa.removeAll();
        for (JButton btn : btnsPecasMesa) {
            painelMesa.add(BorderLayout.CENTER, btn);
        }
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
        b.setPreferredSize(new Dimension(115, 35));
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
        Font fonte = new Font("Comic Sans", Font.PLAIN, 18);
        this.setForeground(Color.WHITE);
        txtStatus = new JTextField("Aguardando todos os participantes se conectarem..");
        txtStatus.setFont(fonte);
        txtQuantidadePecas = new JTextField();
        txtQuantidadePecas.setFont(fonte);
        txtStatus.setBackground(this.getBackground());
        txtQuantidadePecas.setBackground(this.getBackground());
        btnsPecas = new ArrayList();
        btnsPecasMesa = new ArrayList();
        pecasDisponiveisParaCompra = new ArrayList();
        pecasCompradasNaJogada = new ArrayList();
        btnPassarVez = new JButton("Passar Vez");
        JScrollPane scroll= new JScrollPane();
        alterarDesignBotao(btnPassarVez);
        btnPassarVez.setEnabled(false);
        btnComprar = new JButton("Comprar Peça");
        alterarDesignBotao(btnComprar);
        btnComprar.setEnabled(false);
        grupoBotoes = new ButtonGroup();
        rbInserirNaEsquerda = new JRadioButton("Inserir na esquerda");
        rbInserirNaDireita = new JRadioButton("Inserir na direita");
        painelJogador = new JPanel();
        
        painelMesa = new JPanel();
        painelBase = new JPanel();
        painelTopo = new JPanel();
        painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.PAGE_AXIS));
        painelAcoes = new JPanel();
        rbInserirNaEsquerda.setSelected(true);
        grupoBotoes.add(rbInserirNaEsquerda);
        grupoBotoes.add(rbInserirNaDireita);
        painelAcoes.setLayout(new BoxLayout(painelAcoes, BoxLayout.PAGE_AXIS));
        painelAcoes.add(btnPassarVez);
        painelAcoes.add(btnComprar);
        
        painelJogador.add(painelAcoes);
        JPanel painelInserirPeca = new JPanel();
        painelInserirPeca.setLayout(new BoxLayout(painelInserirPeca, BoxLayout.PAGE_AXIS));
        painelInserirPeca.add(rbInserirNaDireita);
        painelInserirPeca.add(rbInserirNaEsquerda);
        painelJogador.add(painelInserirPeca);
        
        for (int i = 0; i < 6; i++) {
            JButton peca = new JButton("");
            peca.setEnabled(false);
            peca.addActionListener(new EnviarMensagemAoServidor());
            alterarDesignBotao(peca);
            peca.setPreferredSize(new Dimension(55, 40));
            btnsPecas.add(peca);
            painelJogador.add(peca);
        }
        painelBase.setLayout(new BoxLayout(painelBase, BoxLayout.PAGE_AXIS));
        JPanel p=new JPanel();
        p.setLayout(new BorderLayout());
        painelBase.add(painelJogador);
        painelTopo.add(txtStatus);
        painelTopo.add(txtQuantidadePecas);
        p.add(BorderLayout.NORTH, painelTopo);
        p.add(BorderLayout.SOUTH, painelBase);
        p.add(BorderLayout.CENTER, painelMesa);
         
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setViewportBorder(new LineBorder(Color.CYAN));
        scroll.getViewport().add(p, null);
        this.add(scroll);
        setVisible(true);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        definirAcoesBotoes();

    }

    private void exibirQuantidadePecasDosJogadores(String quantidadePecasJogadores) {
        txtQuantidadePecas.setText("Qtd de peças: " + quantidadePecasJogadores);
        painelTopo.revalidate();
    }

    public String pecasCompradasNaJogada() {
        String pecasCompradas = "";
        for (int i = 0; i < pecasCompradasNaJogada.size(); i++) {
            pecasCompradas += pecasCompradasNaJogada.get(i) + ",";
        }
        if (!pecasCompradas.equals("")) {
            pecasCompradasNaJogada.clear();
        }
        return pecasCompradas;
    }

    private void definirAcoesBotoes() {

        btnPassarVez.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saida.println("#" + pecasCompradasNaJogada());
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
                    alterarDesignBotao(btnPecaComprada);
                    btnPecaComprada.setPreferredSize(new Dimension(55, 40));
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
            } 
            //ID_MENSAGEM_INICIAL
            else if (mensagens[0].equals("1")) {
                int idJogadorDaVez = Integer.parseInt(mensagens[1]);

                if (idJogadorDaVez == this.id) {
                    habilitarBotoes();
                    txtStatus.setText("Sua vez!");
                } else {
                    desabilitarBotoes();
                    char equipe = (idJogadorDaVez % 2 == 0) ? 'A' : 'B';
                    txtStatus.setText("Aguarde o jogador " + idJogadorDaVez + " - " + " equipe " + equipe + " jogar...");
                }
            }
            //ID_MENSAGEM_INFORMAR_JOGADA
            else if (mensagens[0].equals("2")) {
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
            //ID_MENSAGEM_QTD_PECAS_COMPRADAS
            else if (mensagens[0].equals("3")) {
                int numeroPecasCompradasPeloAdversario = Integer.parseInt(mensagens[1]);
                
                for (int i = 0; i < numeroPecasCompradasPeloAdversario; i++) {
                           pecasDisponiveisParaCompra.remove(0);                        
                    }
                }
            else if(mensagens[0].equals("4")){
                JOptionPane.showMessageDialog(null, "O Jogador "+mensagens[1]+" venceu esta partida. Pontuaç�o: "+mensagens[2]);
                desabilitarBotoes();
                JButton btnNovaPartida = new JButton("Iniciar Nova Partida");
                txtStatus.setVisible(false);
                painelTopo.add(btnNovaPartida);
                alterarDesignBotao(btnNovaPartida);
                painelTopo.revalidate();
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

                saida.println(direita + "#" + pecaClicada.getText() + "#" + pecasQueEstaoNasPontas() + "#" + pecasCompradasNaJogada());
                saida.flush();

                pecaClicada.setVisible(false);
                btnsPecas.remove(pecaClicada);
                desabilitarBotoes();

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
