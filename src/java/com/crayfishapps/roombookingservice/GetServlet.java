package com.crayfishapps.roombookingservice;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author crayfishapps developer
 */
public class GetServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.addHeader("Access-Control-Allow-Origin", "*");        
        response.setContentType("text/plain");
        
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        
        if (user != null) {
            
            String currentUser = user.getUserId();
            
            // list of users
            Query.Filter userFilter = new Query.FilterPredicate("userID", Query.FilterOperator.EQUAL, currentUser);
            Query query = new Query("CardReader").setFilter(userFilter);
            List<Entity> members = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
            
            try (PrintWriter out = response.getWriter()) {
                if (!members.isEmpty()) {
                    out.println("{");
                    String delim = "";
                    for (Entity memberEntity : members) {
                        String information = memberEntity.getProperty("information").toString();
                        String booking = memberEntity.getProperty("user").toString();
                        out.print(delim);
                        out.print("\"" + information + "\"" + ":" + "\"" + booking + "\"");
                        delim = ",\n";
                    }
                    out.println("\n}");
                }

                out.close();
            }
        }
        else {
            try (PrintWriter out = response.getWriter()) {
                out.println("Please sign in.");
                out.close();
            }            
        }
        

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
