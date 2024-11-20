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

    @GET
    @Path("/verificar-fontes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarFontesDentroDoLimite(@QueryParam("idMicrogrid") Long idMicrogrid, @QueryParam("limite") double limite) {
        try {
            boolean resultado = fonteEnergiaBO.verificarFontesDentroDoLimite(idMicrogrid, limite);
            return Response.ok(resultado).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/capacidade-total")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularCapacidadeTotal(@QueryParam("idMicrogrid") Long idMicrogrid) {
        try {
            double capacidadeTotal = fonteEnergiaBO.calcularCapacidadeTotal(idMicrogrid);
            return Response.ok(String.format("{\"capacidadeTotal\": %.2f}", capacidadeTotal)).build();
        } catch (FonteEnergiaNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }


    @GET
    @Path("/relatorio/{idMicrogrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response gerarRelatorioFontes(@PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            fonteEnergiaBO.gerarRelatorioFontes(idMicrogrid);
            return Response.ok("Relatório gerado no console com sucesso.").build();
        } catch (FonteEnergiaNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }
    @POST
    @Path("/validar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validarFonte(FonteEnergiaTO fonte) {
        try {
            fonteEnergiaBO.validarFonte(fonte);
            return Response.ok("Fonte validada com sucesso.").build();
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
    @GET
    @Path("/microgrid/{idMicrogrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByMicrogrid(@PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            ArrayList<FonteEnergiaTO> fontes = fonteEnergiaBO.findByMicrogrid(idMicrogrid);
            return Response.ok(fontes).build();
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
    @GET
    @Path("/capacidade-total-por-microgrid/{idMicrogrid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response calcularCapacidadeTotalPorMicrogrid(@PathParam("idMicrogrid") Long idMicrogrid) {
        try {
            double capacidadeTotal = fonteEnergiaBO.calcularCapacidadeTotalPorMicrogrid(idMicrogrid);
            return Response.ok(String.format("{\"capacidadeTotal\": %.2f}", capacidadeTotal)).build();
        } catch (FonteEnergiaNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado: " + e.getMessage())
                    .build();
        }
    }
}

