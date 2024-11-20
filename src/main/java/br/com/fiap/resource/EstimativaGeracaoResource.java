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
            List<EstimativaGeracaoTO> estimativas = estimativaGeracaoBO.findAll(); // Retorna todas as estimativas
            return Response.ok(estimativas).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            LOGGER.warning("Erro: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOGGER.severe("Erro inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }



    /**
     * Retorna uma estimativa específica com base no ID.
     *
     * @param idEstimativa ID da estimativa.
     * @return Detalhes da estimativa em formato JSON.
     */
    @GET
    @Path("/{idEstimativa}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idEstimativa") Long idEstimativa) {
        if (idEstimativa == null) {
            LOGGER.warning("Parâmetro 'idEstimativa' não foi fornecido.");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro 'idEstimativa' é obrigatório.")
                    .build();
        }
        try {
            EstimativaGeracaoTO estimativa = estimativaGeracaoBO.findById(idEstimativa);
            return Response.ok(estimativa).build();
        } catch (InvalidEstimativaGeracaoException | EstimativaGeracaoNotFoundException e) {
            LOGGER.severe("Erro: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            LOGGER.severe("Erro inesperado: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/{idEstimativa}")
    public Response delete(@PathParam("idEstimativa") Long idEstimativa) {
        if (idEstimativa == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("O parâmetro 'idEstimativa' é obrigatório.").build();
        }
        try {
            boolean deletado = estimativaGeracaoBO.delete(idEstimativa);
            if (deletado) {
                return Response.noContent().build();
            } else {
                throw new EstimativaGeracaoNotFoundException("Estimativa não encontrada para exclusão.");
            }
        } catch (InvalidEstimativaGeracaoException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/projecao-anual")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projetarGeracaoAnual(@QueryParam("idMicrogrid") Long idMicrogrid) {
        if (idMicrogrid == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro 'idMicrogrid' é obrigatório.")
                    .build();
        }
        try {
            double projecao = estimativaGeracaoBO.projetarGeracaoAnual(idMicrogrid);
            return Response.ok(String.format("{\"projecaoAnual\": %.2f}", projecao)).build();
        } catch (InvalidEstimativaGeracaoException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/media-excede")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarSeMediaExcede(@QueryParam("limite") Double limite) {
        if (limite == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("O parâmetro 'limite' é obrigatório.")
                    .build();
        }
        try {
            boolean excede = estimativaGeracaoBO.verificarSeMediaExcede(limite);
            return Response.ok(String.format("{\"excedeLimite\": %b}", excede)).build();
        } catch (InvalidEstimativaGeracaoException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EstimativaGeracaoNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/calcular-media")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMedia(List<EstimativaGeracaoTO> estimativas) {
        try {
            ArrayList<EstimativaGeracaoTO> estimativasArray = new ArrayList<>(estimativas);
            double media = estimativaGeracaoBO.calcularMediaWattsEstimados(estimativasArray);
            return Response.ok(String.format("{\"media\": %.2f}", media)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }



    @GET
    @Path("/filtrar-ano")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filtrarPorAno(@QueryParam("ano") int ano, @QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            ArrayList<EstimativaGeracaoTO> estimativas = new ArrayList<>(estimativaGeracaoBO.findByMicrogrid(idMicrogrid));
            ArrayList<EstimativaGeracaoTO> filtradas = estimativaGeracaoBO.buscarEstimativasPorAno(estimativas, ano);
            return Response.ok(filtradas).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @POST
    @Path("/calcular-media-estimativas")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMediaDeEstimativas(List<EstimativaGeracaoTO> estimativas) {
        try {
            ArrayList<EstimativaGeracaoTO> estimativasArray = new ArrayList<>(estimativas);
            double media = estimativaGeracaoBO.calcularMediaDeEstimativas(estimativasArray);
            return Response.ok(String.format("{\"media\": %.2f}", media)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @POST
    @Path("/relatorio-media")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response gerarRelatorioMedia(List<EstimativaGeracaoTO> estimativas) {
        try {
            ArrayList<EstimativaGeracaoTO> estimativasArray = new ArrayList<>(estimativas);
            estimativaGeracaoBO.gerarRelatorioMedia(estimativasArray);
            return Response.ok("Relatório gerado com sucesso. Verifique os logs para mais detalhes.").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }



}
