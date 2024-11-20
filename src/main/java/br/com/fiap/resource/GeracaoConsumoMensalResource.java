package br.com.fiap.resource;

import br.com.fiap.bo.GeracaoConsumoMensalBO;
import br.com.fiap.exception.GeracaoConsumoMensalNotFoundException;
import br.com.fiap.exception.InvalidGeracaoConsumoMensalException;
import br.com.fiap.to.GeracaoConsumoMensalTO;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;

@Path("/geracao-consumo")
public class GeracaoConsumoMensalResource {

    private final GeracaoConsumoMensalBO geracaoConsumoMensalBO = new GeracaoConsumoMensalBO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            ArrayList<GeracaoConsumoMensalTO> resultado = geracaoConsumoMensalBO.findAll();
            return Response.ok(resultado).build();
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/{idMicrogrid}/{ano}/{mes}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByAnoMes(@PathParam("idMicrogrid") Long idMicrogrid,
                                 @PathParam("ano") Integer ano,
                                 @PathParam("mes") Integer mes) {
        try {
            GeracaoConsumoMensalTO resultado = geracaoConsumoMensalBO.findByAnoMes(idMicrogrid, ano, mes);
            return Response.ok(resultado).build();
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(GeracaoConsumoMensalTO registro) {
        try {
            GeracaoConsumoMensalTO resultado = geracaoConsumoMensalBO.save(registro);
            return Response.status(Response.Status.CREATED).entity(resultado).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/proporcao-geracao-consumo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularProporcaoGeracaoConsumo(@QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            double proporcao = geracaoConsumoMensalBO.calcularProporcaoGeracaoConsumo(idMicrogrid);
            return Response.ok(String.format("{\"proporcao\": %.2f}", proporcao)).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(GeracaoConsumoMensalTO registro) {
        try {
            boolean atualizado = geracaoConsumoMensalBO.update(registro);
            if (atualizado) {
                return Response.ok().build();
            } else {
                throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para atualização.");
            }
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @PUT
    @Path("/{idRegistro}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response atualizarRegistro(@PathParam("idRegistro") Long idRegistro,
                                      GeracaoConsumoMensalTO novosDados) {
        try {
            geracaoConsumoMensalBO.atualizarRegistro(
                    idRegistro,
                    novosDados.getAno(),
                    novosDados.getMes(),
                    novosDados.getWattsGerados(),
                    novosDados.getUnidadeGeracao(),
                    novosDados.getWattsConsumidos(),
                    novosDados.getUnidadeConsumo()
            );
            return Response.ok("Registro atualizado com sucesso.").build();
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }


    @DELETE
    @Path("/{idRegistro}")
    public Response delete(@PathParam("idRegistro") Long idRegistro) {
        try {
            boolean deletado = geracaoConsumoMensalBO.delete(idRegistro);
            if (deletado) {
                return Response.noContent().build();
            } else {
                throw new GeracaoConsumoMensalNotFoundException("Registro não encontrado para exclusão.");
            }
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @GET
    @Path("/analise-media-diferenca")
    @Produces(MediaType.TEXT_PLAIN)
    public Response gerarAnaliseMediaDiferencaWatts(@QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            String analise = geracaoConsumoMensalBO.gerarAnaliseMediaDiferencaWatts(idMicrogrid);
            return Response.ok(analise).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @GET
    @Path("/relatorio-proporcao")
    @Produces(MediaType.TEXT_PLAIN)
    public Response gerarRelatorioProporcaoGeracaoConsumo(@QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            String relatorio = geracaoConsumoMensalBO.gerarRelatorioProporcaoGeracaoConsumo(idMicrogrid);
            return Response.ok(relatorio).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
    @GET
    @Path("/relatorios-automatizados")
    @Produces(MediaType.TEXT_PLAIN)
    public Response gerarRelatoriosAutomatizados(@QueryParam("idMicrogrid") Long idMicrogrid) {
        if (idMicrogrid == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("ID da microgrid é obrigatório.").build();
        }
        try {
            geracaoConsumoMensalBO.gerarRelatoriosAutomatizados(idMicrogrid);
            return Response.ok("Relatórios automatizados gerados com sucesso!").build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }


    @GET
    @Path("/diferenca-media")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularMediaDiferencaWatts(@QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            double media = geracaoConsumoMensalBO.calcularMediaDiferencaWatts(idMicrogrid);
            return Response.ok(media).build();
        } catch (InvalidGeracaoConsumoMensalException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
}
