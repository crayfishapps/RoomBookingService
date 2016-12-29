package com.crayfishapps.roombookingservice;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author crayfishapps developer
 */
public class ReportServlet extends HttpServlet {

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
        response.setContentType("text/plain");
        
        try (PrintWriter out = response.getWriter()) {
            Enumeration<String> parameterNames = request.getParameterNames();
            if (parameterNames.hasMoreElements()) {
                String reader = request.getParameter("reader");
                reader = reader.trim().toLowerCase();
                reader = reader.replaceAll(":", "");
                String card = request.getParameter("card");
                card = card.trim().toLowerCase();
                card = card.replaceAll(":", "");                
                
                DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
                Filter userFilter = new FilterPredicate("serialnumber", FilterOperator.EQUAL, card);
                Query userQuery = new Query("User").setFilter(userFilter);
                List<Entity> users = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
                if (users.isEmpty()) {
                    out.println("Error: Badge is not registerd");
                }
                else {
                    Entity user = users.get(0);
                    String userName = user.getProperty("information").toString();
                                        
                    Filter roomFilter = new FilterPredicate("serialnumber", FilterOperator.EQUAL, reader);
                    Query roomQuery = new Query("CardReader").setFilter(roomFilter);
                    List<Entity> rooms = datastore.prepare(roomQuery).asList(FetchOptions.Builder.withDefaults());
                    if (rooms.isEmpty()) {
                        out.println("Error: Reader is not registerd");
                    }
                    else {
                        Entity room = rooms.get(0);
                        String oldUserName = room.getProperty("user").toString();
                        
                        if (oldUserName.equalsIgnoreCase(userName)) {
                            room.setProperty("user", "");
                            out.println(userName + "\nChecked out");
                        }
                        else {
                            room.setProperty("user", userName);
                            out.println(userName + "\nChecked in");
                        }
                        datastore.put(room);
                    }
                }
 
            }
            out.close();
        }
        catch (Exception e) {
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
