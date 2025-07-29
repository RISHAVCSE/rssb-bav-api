package com.RSSBAMB.API.helper;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RSSBAMB.API.Repo.BookAllotmentAllocationRecordRepo;
import com.RSSBAMB.API.model.BookAllotmentAllocationRecord;

@Service
public class AllotmentAllocationSaleHelper {
	
	@Autowired
	BookAllotmentAllocationRecordRepo bookAllotmentAllocationRecordRepo;
	
	public void logAllocation(String mmsId,int centreCode,String bookName,
			int amount,int allotedQuantity,String allocationType,
			String Remarks ,int previousQuantity, int previousBalance, 
			int currentBalance, int totalQuantity,Date allotedDate, 
			String allotedBy, String type) {
		BookAllotmentAllocationRecord record=new BookAllotmentAllocationRecord();
		record.setMmsId(mmsId);
		record.setCentreCode(centreCode);
		record.setBookName(bookName);
		record.setAmount(amount);
		record.setAllotedQuantity(allotedQuantity);
		record.setAllocationType(allocationType);
		record.setRemarks(Remarks);
		record.setPreviousQuantity(previousQuantity);
		record.setPreviousBalance(previousBalance);
		record.setCurrentBalance(currentBalance);
		record.setTotalQuantity(totalQuantity);
		record.setAllotedDate(allotedDate);
		record.setAllotedBy(allotedBy);
		record.setType(type);
		bookAllotmentAllocationRecordRepo.save(record);
	}
	

}
