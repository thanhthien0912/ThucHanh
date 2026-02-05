package com.phanthanhthien.cmp3025.bookstore.services;

import com.phanthanhthien.cmp3025.bookstore.entities.Counter;
import com.phanthanhthien.cmp3025.bookstore.repository.BookRepository;
import com.phanthanhthien.cmp3025.bookstore.repository.CategoryRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CounterService - Quản lý Auto Increment cho MongoDB
 *
 * Tạo ID tự động tăng cho các collection
 *
 * @author Phan Thanh Thien
 * @version 1.0.0
 */
@Service
public class CounterService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final String DB_NAME = "bookstore_db_new";
    private static final String COUNTER_COLLECTION = "counters";

    /**
     * Lấy sequence ID tiếp theo cho một collection
     *
     * @param collectionName Tên collection (ví dụ: "books", "categories")
     * @return ID tiếp theo (Long)
     */
    public Long getNextSequence(String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> counterCollection = database.getCollection(COUNTER_COLLECTION);

        // Tìm và cập nhật counter trong một operation (atomic)
        Bson filter = new Document("_id", collectionName);
        Bson update = Updates.inc("seq", 1L);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions()
            .returnDocument(ReturnDocument.AFTER)
            .upsert(true);

        Document result = counterCollection.findOneAndUpdate(filter, update, options);

        // Trả về giá trị seq mới
        return result.getLong("seq");
    }

    /**
     * Reset counter về giá trị cụ thể
     * Dùng khi cần reset sequence
     */
    public void resetCounter(String collectionName, Long value) {
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> counterCollection = database.getCollection(COUNTER_COLLECTION);

        counterCollection.replaceOne(
            new Document("_id", collectionName),
            new Document("_id", collectionName)
                .append("seq", value)
        );
    }

    /**
     * Xóa một counter
     */
    public void deleteCounter(String collectionName) {
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> counterCollection = database.getCollection(COUNTER_COLLECTION);

        counterCollection.deleteOne(new Document("_id", collectionName));
    }

    /**
     * Reset tất cả counters về 0
     */
    public void resetAllCounters() {
        MongoDatabase database = mongoClient.getDatabase(DB_NAME);
        MongoCollection<Document> counterCollection = database.getCollection(COUNTER_COLLECTION);

        counterCollection.deleteMany(new Document());
    }
}
