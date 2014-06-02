/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientetcp;

/**
 *
 * @author jessica
 */
//Formato das mensagens que o cliente pode enviar ao servidor
public class TipoMensagem {
   static int ID_MENSAGEM_PECA_JOGADA = 0;
  //0#posicao#peca#pecasQueEstaoNasPontas#PecasCompradasNaJogada
   static int ID_MENSAGEM_PASSAR_VEZ = 1;
  //1#pecasCompradasNaJogada
   static int ID_MENSAGEM_NOVA_PARTIDA = 2;
  //2#
   
}
