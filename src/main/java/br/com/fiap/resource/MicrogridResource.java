package br.com.fiap.resource;

import br.com.fiap.bo.MicrogridBO;
import br.com.fiap.exception.MicrogridNotFoundException;
import br.com.fiap.exception.InvalidMicrogridException;
import br.com.fiap.to.MicrogridTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/microgrid")
public class MicrogridResource {

    private final MicrogridBO microgridBO = new MicrogridBO();

    /**
     * Busca todas as microgrids.
     *
     * @return Lista de microgrids em formato JSON.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        System.out.println("Requisição recebida: GET /microgrid");
        try {
            ArrayList<MicrogridTO> resultado = microgridBO.findAll();
            System.out.println("Microgrids encontradas: " + resultado.size());
            return Response.ok(resultado).build();
        } catch (MicrogridNotFoundException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Busca uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Microgrid correspondente ao ID.
     */
    @GET
    @Path("/{idMicrogrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idMicrogrid") Long idMicrogrid) {
        System.out.println("Requisição recebida: GET /microgrid/" + idMicrogrid);
        try {
            MicrogridTO resultado = microgridBO.findById(idMicrogrid);
            return Response.ok(resultado).build();
        } catch (MicrogridNotFoundException | InvalidMicrogridException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Cria uma nova microgrid.
     *
     * @param microgrid Objeto da microgrid enviado no corpo da requisição.
     * @return Microgrid criada.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(MicrogridTO microgrid) {
        System.out.println("Requisição recebida: POST /microgrid");
        try {
            MicrogridTO resultado = microgridBO.save(microgrid);
            return Response.status(Response.Status.CREATED).entity(resultado).build();
        } catch (InvalidMicrogridException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Atualiza uma microgrid existente.
     *
     * @param microgrid Microgrid a ser atualizada.
     * @return Resposta indicando sucesso ou erro.
     */
    @PUT
    @Path("/{idMicrogrid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(MicrogridTO microgrid) {
        System.out.println("Requisição recebida: PUT /microgrid");
        try {
            boolean atualizado = microgridBO.update(microgrid);
            if (atualizado) {
                return Response.ok().build();
            } else {
                throw new MicrogridNotFoundException("Microgrid não encontrada para atualização.");
            }
        } catch (MicrogridNotFoundException | InvalidMicrogridException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Exclui uma microgrid pelo ID.
     *
     * @param idMicrogrid ID da microgrid.
     * @return Resposta indicando sucesso ou erro.
     */
    @DELETE
    @Path("/{idMicrogrid}")
    public Response delete(@PathParam("idMicrogrid") Long idMicrogrid) {
        System.out.println("Requisição recebida: DELETE /microgrid/" + idMicrogrid);
        try {
            boolean deletado = microgridBO.delete(idMicrogrid);
            if (deletado) {
                return Response.noContent().build();
            } else {
                throw new MicrogridNotFoundException("Microgrid não encontrada para exclusão.");
            }
        } catch (MicrogridNotFoundException | InvalidMicrogridException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Verifica a densidade de habitantes por residência de uma microgrid.
     *
     * @param idMicrogrid ID da microgrid.
     * @param limite      Limite de densidade.
     * @return true se dentro do limite.
     */
    @GET
    @Path("/{idMicrogrid}/densidade-verificar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarDensidade(@PathParam("idMicrogrid") Long idMicrogrid, @QueryParam("limite") double limite) {
        System.out.println("Requisição recebida: GET /microgrid/" + idMicrogrid + "/densidade-verificar");
        try {
            MicrogridTO microgrid = microgridBO.findById(idMicrogrid);
            boolean dentroDoLimite = microgrid.verificarDensidadeDentroDoLimite(limite);
            return Response.ok(dentroDoLimite).build();
        } catch (MicrogridNotFoundException | InvalidMicrogridException e) {
            System.err.println("Erro: " + e.getMessage());
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Método utilitário para construir respostas de erro.
     *
     * @param status Código de status HTTP.
     * @param mensagem Mensagem de erro.
     * @return Resposta de erro.
     */
    private Response buildErrorResponse(Response.Status status, String mensagem) {
        return Response.status(status)
                .entity("{\"erro\":\"" + mensagem + "\"}")
                .build();
    }
}