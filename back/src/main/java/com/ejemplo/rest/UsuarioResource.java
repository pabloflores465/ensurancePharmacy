package com.ejemplo.rest;

import com.ejemplo.dao.UsuarioDAO;
import com.ejemplo.modelo.Usuario;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/usuarios")  // Ruta base del servicio
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();  // Instancia del DAO

    @GET
    @Path("/listar")
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.obtenerTodosLosUsuarios();  // Consulta la base de datos
    }

    @GET
    @Path("/{id}")  // Obtener usuario por ID
    public Usuario obtenerUsuario(@PathParam("id") int id) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorId(id);
        if (usuario != null) {
            return usuario;
        } else {
            throw new WebApplicationException("Usuario no encontrado", 404);
        }
    }
}