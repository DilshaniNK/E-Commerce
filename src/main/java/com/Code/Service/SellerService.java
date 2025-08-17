package com.Code.Service;

import com.Code.domain.AccountStatus;
import com.Code.exception.sellerException;
import com.Code.model.Seller;

import java.util.List;

public interface SellerService {

    Seller getSellerProfile(String jwt) throws sellerException;
    Seller createSeller(Seller seller) throws sellerException;
    Seller getSellerById(Long id) throws sellerException;
    Seller getSellerByEmail(String email) throws sellerException;
    List<Seller> getAllSellers(AccountStatus status);
    Seller updateSeller(Long id , Seller seller) throws sellerException;
    void deleteSeller(Long id) throws sellerException;
    Seller verifyEmail(String email, String otp) throws sellerException;
    Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws sellerException;



}
