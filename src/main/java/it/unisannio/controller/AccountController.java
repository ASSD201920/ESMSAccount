package it.unisannio.controller;

import it.unisannio.model.Account;
import it.unisannio.service.BranchLocal;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Consumes("text/plain")
@Produces("text/plain")
@Path("/accounts")
public class AccountController  {

	@EJB
	private BranchLocal branch;

	@Resource UserTransaction utx; // To handle user transactions from a Web component


	public AccountController() {
		super();

	}

	@GET
	public Response test() {
		System.out.println("test");
		try {
			branch.getAccount(1);
			return Response.ok().build();
		} catch (Exception e) {return Response.status(500).build();}

	}

	@POST
	@Path("/{accountId}/deposits")
	public Response deposit(@PathParam("accountId") int accountNum, double amount) {
		try {

			branch.deposit(accountNum, amount);

			return Response.ok().build();
		} catch (Exception e) {
			System.out.println(e);
			return Response.status(500).build();
		}
	}


	@POST
	@Path("/{accountId}/withdraws")
	public Response withdraw(@PathParam("accountId") int accountNum, double amount) {
		try {
			branch.withdraw(accountNum, amount);

			return Response.ok().build();
		} catch (Exception e) {
			System.out.println(e);
			return Response.status(500).build();
		}
	}

	@GET
	@Path("/{accountId}/")
	public Response getBalance(@PathParam("accountId") int accountNum) {
		Account a = branch.getAccount(accountNum);

		try {
			return Response.ok(a.getBalance()).lastModified(a.getLastModified()).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@PUT
	@Path("/{accountId}/")
	public Response setBalance(@PathParam("accountId") int accountNum, double amount, @Context Request request) {
		Account a = branch.getAccount(accountNum);
		Response.ResponseBuilder builder = null;
		try {
			builder = request.evaluatePreconditions(a.getLastModified());
			if (builder != null) {
				branch.getAccount(accountNum).setBalance(amount);
			}
			return builder.status(204).build();

		} catch (Exception e) {
			return builder.status(500).build();
		}
	}

	@POST
	@Path("/")
	public Response createAccount(@QueryParam("cf") String custCF, double amount) {
		try {
			return Response.created(new URI("/accounts/"+branch.createAccount(custCF, amount))).build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}

	@POST
	@Path("/transfers")
	public Response transfer(@QueryParam("source") int srcAccount, @QueryParam("destination") int dstAccount, double amount) {
		try {

			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(500).build();
		}
	}
}
