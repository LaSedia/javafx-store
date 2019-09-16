package model.services;

import java.util.List;

import db.DbException;
import model.entities.Seller;
import model.dao.DaoFactory;
import model.dao.SellerDao;

public class SellerService {
	
	private SellerDao dao = DaoFactory.createSellerDao();
	
	public List<Seller> findAll()	{
		return dao.findAll();
	}
	
	public void saveOrUpdate(Seller obj)	{
		if (obj.getId() == null)	{
			dao.insert(obj);
		}
		else	{
			dao.update(obj);
		}
	}
	
	public void remove(Seller obj)	{
		if (obj.getId() == null)	{
			throw new DbException("Seller not found");
		}
		else	{
			dao.deleteById(obj.getId());
		}
	}

}
