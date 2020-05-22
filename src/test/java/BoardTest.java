/**
 * 
 */


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import es.codeurjc.ais.tictactoe.Board;
import es.codeurjc.ais.tictactoe.Player;
import es.codeurjc.ais.tictactoe.TicTacToeGame;

/**
 * Comprueba que la clase Board implementa correctamente la detección de ganar o empatar.
 * @author
 *
 */
@DisplayName("Clase Board: metodos para ganar y empatar")
class BoardTest {
	
	Board board;
	TicTacToeGame ticTac;
	Player player1;
	Player player2;
	
	@BeforeEach
	void setUp() throws Exception {
		
		board = new Board();
		board.enableAll();

		ticTac = new TicTacToeGame();

		player1 = new Player(1,"O","Jug1");
		player2 = new Player(2,"X","Jug2");
		
		ticTac.addPlayer(player1);
		ticTac.addPlayer(player2);
		//Inyecta el objeto board en el juego
		//ReflectionTestUtils.setField(ticTac, "board", board); 
		ticTac.setBoard(board);  //Se ha creado un nuevo metodo setBoard en la clase TicTacToeGame
	}
	
	@Nested
	@DisplayName("Clase Board: comprueba las posiciones del ganador")
	@TestInstance(Lifecycle.PER_CLASS)
	class getCellsIfWin{
		
		/**
		 * Proveedor de argumentos para el metodo test.
		 * @return Stream<Arguments>
		 */
		Stream<Arguments> turnosProvider() {
		    return Stream.of(
		    		Arguments.of(-1, Arrays.asList(0,1,2,3,5,4,7,8,6), null),
		    		Arguments.of(-1, Arrays.asList(0,3,1,2,5,4,6,7,8), null),
		    		Arguments.of(-1, Arrays.asList(4,0,3,5,2,6,8,1,7), null),
		    		Arguments.of(0, Arrays.asList(0,8,1,7,2), Arrays.asList(0,1,2)),
		    		Arguments.of(0, Arrays.asList(4,1,0,5,8), Arrays.asList(0,4,8)),
		    		Arguments.of(0, Arrays.asList(4,0,1,5,7), Arrays.asList(1,4,7)),
		    		Arguments.of(1, Arrays.asList(0,3,8,4,2,5), Arrays.asList(3,4,5)),
		    		Arguments.of(1, Arrays.asList(0,4,3,6,1,2), Arrays.asList(6,4,2)),
		    		Arguments.of(1, Arrays.asList(4,0,1,3,5,6), Arrays.asList(0,3,6))
		    );
		}
		
		/**
		 * Casos de prueba para el metodo getCellsIfWinner.
		 * Comprueba que el metodo devuelve null si ningun jugador gana al finalizar los turnos.
		 * 
		 */
		@ParameterizedTest
		@MethodSource("turnosProvider")
		@DisplayName("Clase Board: metodo getCellsIfWin")
		void test(int jugGanador,List<Integer> turnos,List<Integer> resultEsperado) {
			//GIVEN       Arrange / given: Definir el estado inicial del SUT. Las condiciones del test	
			//WHEN      Act / when: Actuar sobre esa SUT. Ejercitarlo.
			for(Integer turno: turnos) ticTac.mark(turno);

			int[] lineaResultado = board.getCellsIfWinner("O");
			int[] lineaResultado2 = board.getCellsIfWinner("X");
			int[] resultadoEsperado = null;
			
			if(resultEsperado != null) {  //Convirtiendo de lista a array de enteros
				resultadoEsperado = new int[resultEsperado.size()];
				for(int i = 0; i < resultEsperado.size();i++) resultadoEsperado[i] = resultEsperado.get(i);
			}

			//Assert / then: Verificar que el comportamiento obtenido es el esperado
			if(jugGanador == -1) {			//Ningun jugador gana
				assertThat(lineaResultado, nullValue());
				assertThat(lineaResultado2, nullValue());
			}else if(jugGanador == 0) {		//Gana el jugador 1
				assertThat(lineaResultado, is(resultadoEsperado));
				assertThat(lineaResultado2, nullValue());
			}else if(jugGanador == 1) {		//Gana el jugador 2
				assertThat(lineaResultado2, is(resultadoEsperado));
				assertThat(lineaResultado, nullValue());
			}
		}
	}
		
	@Nested
	@DisplayName("Clase Board: comprueba el empate")
	@TestInstance(PER_CLASS)
	class checkDraw{
		
		/**
		 * Proveedor de argumentos para el metodo test.
		 * @return Stream<Arguments>
		 */
		Stream<Arguments> turnosProvider() {
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
		 * Caso de prueba checkDraw.
		 * Comprueba que la clase implementa correctamente el empate.
		 * Ha de ejecutarse al final
		 */
		@ParameterizedTest
		@MethodSource("turnosProvider")
		@DisplayName("Clase Board: metodo checkDraw")
		void test2(int jugGanador,List<Integer> turnos) {
			//GIVEN
			//WHEN
			for(Integer turno: turnos) ticTac.mark(turno);

			boolean empate = board.checkDraw();

			//THEN
			if(jugGanador == -1) {			//Ningun jugador gana
				assertTrue(empate);
			}else if(jugGanador == 0) {		//Gana el jugador 1
				assertFalse(empate);
			}else if(jugGanador == 1) {		//Gana el jugador 2
				assertFalse(empate);
			}

		}

	}

}