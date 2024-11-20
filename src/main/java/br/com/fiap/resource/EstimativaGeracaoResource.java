package br.com.fiap.resource;

import br.com.fiap.bo.EstimativaGeracaoBO;
import br.com.fiap.to.EstimativaGeracaoTO;
import br.com.fiap.exception.InvalidEstimativaGeracaoException;
import br.com.fiap.exception.EstimativaGeracaoNotFoundException;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/estimativa-geracao")
public class EstimativaGeracaoResource {

    private static final Logger LOGGER = Logger.getLogger(EstimativaGeracaoResource.class.getName());
    private final EstimativaGeracaoBO estimativaGeracaoBO = new EstimativaGeracaoBO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            LOGGER.info("Buscando todas as estimativas.");
            List<EstimativaGeracaoTO> estimativas = estimativaGeracaoBO.findAll();
            return Response.ok(estimativas).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            LOGGER.warning("Nenhuma estimativa encontrada: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOGGER.severe("Erro inesperado ao buscar estimativas: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\": \"Erro inesperado: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{idEstimativa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idEstimativa") Long idEstimativa) {
        if (idEstimativa == null) {
            LOGGER.warning("Parâmetro 'idEstimativa' não foi fornecido.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"O parâmetro 'idEstimativa' é obrigatório.\"}")
                    .build();
        }
        try {
            EstimativaGeracaoTO estimativa = estimativaGeracaoBO.findById(idEstimativa);
            return Response.ok(estimativa).build();
        } catch (EstimativaGeracaoNotFoundException | InvalidEstimativaGeracaoException e) {
            LOGGER.warning("Erro ao buscar estimativa por ID: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOGGER.severe("Erro inesperado ao buscar estimativa: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\": \"Erro inesperado: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{idMicrogrid}/calcular-media")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMediaPorIdMicrogrid(@PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            ArrayList<EstimativaGeracaoTO> estimativas = estimativaGeracaoBO.findByMicrogrid(idMicrogrid);
            double media = estimativaGeracaoBO.calcularMediaDeEstimativas(estimativas);
            return Response.ok(String.format("{\"media\": %.2f}", media)).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"erro\": \"Erro inesperado: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}

