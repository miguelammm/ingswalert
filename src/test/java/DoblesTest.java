
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

import es.codeurjc.ais.tictactoe.Connection;
import es.codeurjc.ais.tictactoe.Player;
import es.codeurjc.ais.tictactoe.TicTacToeGame;
import es.codeurjc.ais.tictactoe.TicTacToeGame.EventType;
import es.codeurjc.ais.tictactoe.TicTacToeGame.WinnerValue;

/**
 * Casos de prueba que comprueban que la clase TicTacToeGame implementa de forma adecuada el juego.
 * @author
 *
 */
@DisplayName("Clase TicTacToeGame: funcionamiento del juego")
class DoblesTest {
	TicTacToeGame ticTac;
	Connection conexion1;
	Connection conexion2;
	Player player1;
	Player player2;
	
	@BeforeEach
	void setUp() throws Exception {
		ticTac = new TicTacToeGame();
		conexion1 = mock(Connection.class);	//Doble de la comunicación Websocket
		conexion2 = mock(Connection.class);	//Una por navegador

		ticTac.addConnection(conexion1);
		ticTac.addConnection(conexion2);
		player1 = new Player(0,"O","Jug1");
		player2 = new Player(1,"X","Jug2");
	}
	
	@Test
	@DisplayName("Evento JOIN_GAME")
	void test() {		
		ticTac.addPlayer(player1);
		//Comprobar que se ha efectuado el envio del evento con un jugador
		verify(conexion1).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItems(player1)));
		reset(conexion2);
		reset(conexion1);
		ticTac.addPlayer(player2);
		verify(conexion2).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItems(player1, player2)));
		verify(conexion1).sendEvent(eq(EventType.JOIN_GAME), argThat(hasItems(player1, player2)));

	}

	/**
	 * Proveedor de argumentos para el metodo test.
	 * @return Stream<Arguments>
	 */
	static Stream<Arguments> turnosProvider() {
	    return Stream.of(
	    		Arguments.of(-1, Arrays.asList(0,1,2,3,5,4,7,8,6)),
	    		Arguments.of(-1, Arrays.asList(0,3,1,2,5,4,6,7,8)),
	    		Arguments.of(-1, Arrays.asList(4,0,3,5,2,6,8,1,7)),
	    		Arguments.of(0, Arrays.asList(0,8,1,7,2)),
	    		Arguments.of(0, Arrays.asList(4,1,0,5,8)),
	    		Arguments.of(0, Arrays.asList(4,0,1,5,7)),
	    		Arguments.of(1, Arrays.asList(0,3,8,4,2,5)),
	    		Arguments.of(1, Arrays.asList(0,4,3,6,1,2)),
	    		Arguments.of(1, Arrays.asList(4,0,1,3,5,6))
	    );
	}
	
	/**
	 * Casos de prueba para el metodo getCellsIfWinner.
	 * Comprueba que el metodo devuelve null si ningun jugador gana al finalizar los turnos.
	 * 
	 */
	@ParameterizedTest
	@MethodSource("turnosProvider")
	@DisplayName("Eventosventos MARK, SET_TURN y GAME_OVER")
	void test(int jugGanador, List<Integer> turnos) {
		//GIVEN
		//llamar a los métodos de la clase simulando los mensajes 
		//    que llegan de los navegadores durante el uso normal de la aplicación
		
		ticTac.addPlayer(player1);
		ticTac.addPlayer(player2);

		InOrder inOrder = Mockito.inOrder(conexion1);
		InOrder inOrder2 = Mockito.inOrder(conexion2);
		
		//Turnos de los jugadores. Comprueba que se marca y el turno cambia
		int i = 0;
		Integer turnoFinal = turnos.get(turnos.size()-1);
		
		for(Integer turno: turnos) {
			
			reset(conexion1);
			reset(conexion2);
			ticTac.mark(turno);

			if(i%2 == 0) {	//Marca jugador 1
				inOrder.verify(conexion1).sendEvent(eq(EventType.MARK), any());					//Ha marcado el jug1
				inOrder2.verify(conexion2).sendEvent(eq(EventType.MARK), any());
				if(turnoFinal!=turno) {
					inOrder.verify(conexion1).sendEvent(eq(EventType.SET_TURN), eq(player2));	//Cambia el turno a jug2
					inOrder2.verify(conexion2).sendEvent(eq(EventType.SET_TURN), eq(player2));
				}

			}else {			//Marca jugador 2
				inOrder.verify(conexion1).sendEvent(eq(EventType.MARK), any()); 				//Ha marcado el jug1
				inOrder2.verify(conexion2).sendEvent(eq(EventType.MARK), any());
				if(turnoFinal!=turno) {
					inOrder.verify(conexion1).sendEvent(eq(EventType.SET_TURN), eq(player1));	//Cambia el turno a jug2
					inOrder2.verify(conexion2).sendEvent(eq(EventType.SET_TURN), eq(player1));
				}
			}
			i++;
			
		}
		 
		//Comprobar que el juego acaba
		ArgumentCaptor<WinnerValue> captura = ArgumentCaptor.forClass(WinnerValue.class);
		ArgumentCaptor<WinnerValue> captura2 = ArgumentCaptor.forClass(WinnerValue.class);
		verify(conexion1).sendEvent(eq(EventType.GAME_OVER), captura.capture());
		verify(conexion2).sendEvent(eq(EventType.GAME_OVER), captura2.capture());
		Object winnerValue = captura.getValue();
		Object winnerValue2 = captura2.getValue();
		
		
		if(jugGanador == -1) { //Si hay empate
			assertThat(winnerValue, nullValue());
			assertThat(winnerValue2, nullValue());
		}else if(jugGanador == 0) { //Si gana jug1
			assertThat(winnerValue, notNullValue());
			assertThat(winnerValue2, notNullValue());
		}else { //Si gana jug2
			assertThat(winnerValue, notNullValue());
			assertThat(winnerValue2, notNullValue());
		}
	
	}

}
