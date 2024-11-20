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
    @Path("/{idRegistro}/{idMicrogrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idRegistro") Long idRegistro,
                             @PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            // Você pode usar o idMicrogrid para validações adicionais, se necessário
            GeracaoConsumoMensalTO resultado = geracaoConsumoMensalBO.findById(idRegistro);

            // Caso queira validar o idMicrogrid
            if (!resultado.getIdMicrogrid().equals(idMicrogrid)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O registro encontrado não pertence ao microgrid especificado.")
                        .build();
            }

            return Response.ok(resultado).build();
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
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

    @PUT
    @Path("/{idRegistro}/{idMicrogrid}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("idRegistro") Long idRegistro,
                           @PathParam("idMicrogrid") Long idMicrogrid,
                           GeracaoConsumoMensalTO registro) {
        try {
            // Define os valores no objeto recebido
            registro.setIdRegistro(idRegistro);
            registro.setIdMicrogrid(idMicrogrid);

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
    @DELETE
    @Path("/{idRegistro}/{idMicrogrid}")
    public Response delete(@PathParam("idRegistro") Long idRegistro,
                           @PathParam("idMicrogrid") Long idMicrogrid) {
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
    @Path("/{idRegistro}/{idMicrogrid}/diferenca")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularDiferencaWatts(@PathParam("idRegistro") Long idRegistro,
                                           @PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            GeracaoConsumoMensalTO registro = geracaoConsumoMensalBO.findById(idRegistro);

            // Valida se o registro pertence ao microgrid especificado
            if (!registro.getIdMicrogrid().equals(idMicrogrid)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("O registro encontrado não pertence ao microgrid especificado.")
                        .build();
            }

            // Calcula a diferença de watts
            double diferenca = registro.calcularDiferencaWatts();
            return Response.ok(String.format("{\"diferencaWatts\": %.2f}", diferenca)).build();
        } catch (GeracaoConsumoMensalNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro inesperado: " + e.getMessage()).build();
        }
    }
}
