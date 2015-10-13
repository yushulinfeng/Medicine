package org.outing.medicine.tools.connect;

/**
 * 状态信息（如请求通过、失败与原因等）
 * 
 * @author Sun Yu Lin
 */
public class AnStatus {

	/**
	 * 通过(状态，原因)构造AnStatus
	 * 
	 * @param status
	 *            状态信息
	 * @param reason
	 *            相关原因
	 */
	public AnStatus(boolean status, String reason) {
		this.status = status;
		this.reason = reason;
	}

	// 核心变量/////////////////////////////////////////
	private boolean status;// 状态
	private String reason;// 原因

	// //////////////////////////////////////////////

	// getter
	public boolean getStatus() {
		return status;
	}

	public String getReason() {
		return reason;
	}

}
