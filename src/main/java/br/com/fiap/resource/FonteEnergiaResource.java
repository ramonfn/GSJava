package br.com.fiap.resource;

import br.com.fiap.bo.FonteEnergiaBO;
import br.com.fiap.to.FonteEnergiaTO;
import br.com.fiap.exception.FonteEnergiaNotFoundException;
import br.com.fiap.exception.InvalidFonteEnergiaException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/fonte-energia")
public class FonteEnergiaResource {

    private FonteEnergiaBO fonteEnergiaBO = new FonteEnergiaBO();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        ArrayList<FonteEnergiaTO> resultado = fonteEnergiaBO.findAll();
        if (resultado != null && !resultado.isEmpty()) {
            return Response.ok(resultado).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Nenhuma fonte de energia encontrada.")
                    .build();
        }
    }

    @GET
    @Path("/{idFonte}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("idFonte") Long idFonte) {
        try {
            FonteEnergiaTO resultado = fonteEnergiaBO.findById(idFonte);
            return Response.ok(resultado).build();
        } catch (FonteEnergiaNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (InvalidFonteEnergiaException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(FonteEnergiaTO fonteEnergia) {
        try {
            FonteEnergiaTO resultado = fonteEnergiaBO.save(fonteEnergia);
            return Response.status(Response.Status.CREATED).entity(resultado).build();
        } catch (InvalidFonteEnergiaException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{idFonte}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(FonteEnergiaTO fonteEnergia) {
        try {
            boolean atualizado = fonteEnergiaBO.update(fonteEnergia);
            if (atualizado) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Fonte de energia não encontrada para atualização.")
                        .build();
            }
        } catch (InvalidFonteEnergiaException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{idFonte}")
    public Response delete(@PathParam("idFonte") Long idFonte) {
        try {
            boolean deletado = fonteEnergiaBO.delete(idFonte);
            if (deletado) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Fonte de energia não encontrada para exclusão.")
                        .build();
            }
        } catch (InvalidFonteEnergiaException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }

}

