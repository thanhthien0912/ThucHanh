package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Book;
import com.phanthanhthien.cmp3025.bookstore.entities.Cart;
import com.phanthanhthien.cmp3025.bookstore.entities.CartItem;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Cart Service - Xử lý logic giỏ hàng
 * Author: Phan Thanh Thien - MSSV: 2280603036
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Lấy giỏ hàng của user
     */
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(userId);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Thêm sách vào giỏ hàng
     */
    public Cart addToCart(String userId, String bookId, int quantity) {
        Cart cart = getCartByUserId(userId);

        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy sách với ID: " + bookId);
        }

        Book book = bookOpt.get();

        // Kiểm tra số lượng tồn kho
        if (book.getStock() < quantity) {
            throw new RuntimeException("Số lượng tồn kho không đủ. Còn lại: " + book.getStock());
        }

        CartItem item = new CartItem(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getImageUrl(),
                book.getPrice(),
                quantity);

        cart.addItem(item);
        return cartRepository.save(cart);
    }

    /**
     * Cập nhật số lượng
     */
    public Cart updateQuantity(String userId, String bookId, int quantity) {
        Cart cart = getCartByUserId(userId);

        if (quantity > 0) {
            // Kiểm tra số lượng tồn kho
            Optional<Book> bookOpt = bookRepository.findById(bookId);
            if (bookOpt.isPresent() && bookOpt.get().getStock() < quantity) {
                throw new RuntimeException("Số lượng tồn kho không đủ. Còn lại: " + bookOpt.get().getStock());
            }
        }

        cart.updateQuantity(bookId, quantity);
        return cartRepository.save(cart);
    }

    /**
     * Xóa sách khỏi giỏ hàng
     */
    public Cart removeFromCart(String userId, String bookId) {
        Cart cart = getCartByUserId(userId);
        cart.removeItem(bookId);
        return cartRepository.save(cart);
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        cart.clear();
        cartRepository.save(cart);
    }

    /**
     * Đếm số item trong giỏ
     */
    public int getCartItemCount(String userId) {
        try {
            Cart cart = cartRepository.findByUserId(userId).orElse(null);
            return cart != null ? cart.getTotalItems() : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
