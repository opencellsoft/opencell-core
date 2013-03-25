/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.BaseEntity;

/**
 * Excel converter class. This class exports data from dataTable to excel file
 * 
 * @author Gediminas Ubartas
 * @created 2010.06.10
 */
@Named
public class ExcelConverter {

	private int CUSTOM_CELL_WIDTH = 40;
	private int START_ROW = 1;

	private ByteArrayOutputStream b = new ByteArrayOutputStream();
	private WritableWorkbook workbook;
	private WritableSheet sheet;

	@SuppressWarnings("rawtypes")
	private PaginationDataModel dataModel;

	@SuppressWarnings({ "rawtypes" })
	private BaseBean dataListBean;

	@Inject
    private ResourceBundle resourceMessages;
	
	/**
	 * Generates file for export
	 * 
	 * @param dataModel
	 *            filtered data model
	 * @param backingBean
	 *            Entities Bean
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	@SuppressWarnings("rawtypes")
	public void export(PaginationDataModel dataModel, BaseBean backingBean)
			throws RowsExceededException, WriteException {
		this.dataModel = dataModel;
		this.dataListBean = backingBean;
		export();
	}

	/**
	 * Generates file for export
	 * 
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	@SuppressWarnings("unchecked")
	public void export() throws RowsExceededException, WriteException {

		try {
			workbook = Workbook.createWorkbook(b);
			sheet = workbook.createSheet("Sheet", 0);
			generateHeader();
			Set<Serializable> keys = dataModel.getKeySet();
			Iterator<Serializable> it = keys.iterator();
			int rowNumber = START_ROW;
			while (it.hasNext() == true) {
				Serializable str = it.next();
				Object rowData = dataModel.getRowData(str);
				processRow(rowNumber++, (BaseEntity) rowData);
			}
			workbook.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				redirectExport();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

	}

	/**
	 * Returns all entity fields
	 * 
	 * @return List of Entity fields
	 */
	public Field[] getEntityFields() {
		return dataListBean.getClazz().getDeclaredFields();
	}

	/**
	 * Generates document header
	 * 
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	public void generateHeader() throws RowsExceededException, WriteException {
		Field[] entityFields = getEntityFields();
		for (int i = 1; i < entityFields.length; i++) {
			sheet.setColumnView(i - 1, CUSTOM_CELL_WIDTH);
			Label label = new Label(i - 1, 0, getMessage(getMessageText(entityFields[i].getName())));
			sheet.addCell(label);
		}
	}

	/**
	 * Getting value from Messages.properties file
	 * 
	 * @param messageText
	 *            text to find and get label from Messages.properties file
	 * 
	 */
	public String getMessage(String messageText) {
		try {
			return resourceMessages.getString(messageText);

		} catch (Throwable t) {
			return "Error while finding label " + messageText;
		}

	}

	/**
	 * Generating caption to get from Messages.properties file
	 * 
	 * @param entityField
	 *            variable name of entity field
	 * 
	 */
	public String getMessageText(String entityField) {
		String className = dataListBean.getClazz().getSimpleName();
		StringBuilder sb = new StringBuilder(className);
		char[] dst = new char[1];
		sb.getChars(0, 1, dst, 0);
		sb.replace(0, 1, new String(dst).toLowerCase());
		sb.append(".");
		sb.append(entityField);
		return sb.toString();
	}

	/**
	 * Processes one row wit Entity data
	 * 
	 * @param rowIndex
	 *            row number where add cell
	 * @param rowData
	 *            Object with data to procces in row
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * 
	 */
	public void processRow(int rowIndex, BaseEntity rowData) throws RowsExceededException,
			WriteException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Field[] entityFields = getEntityFields();
		for (int i = 1; i < entityFields.length; i++) {
			String cellData = "";
			if (isEnum(entityFields[i].getName())) {
				String methodName = getMethodName(getEnumMethodName(entityFields[i].getName()));
				cellData = getMessage(invokeMethod(rowData, methodName));
			} else {
				String methodName = getMethodName(entityFields[i].getName());
				cellData = invokeMethod(rowData, methodName);
			}

			try {
				Label label = new Label(i - 1, rowIndex, cellData);
				sheet.addCell(label);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Checks if variable is Enum
	 * 
	 * @param variableName
	 *            variable name to check if it is Enum
	 */
	public boolean isEnum(String variableName) {

		if (variableName.endsWith("Id")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Returns method name, to get Enum label
	 * 
	 * @param variableName
	 *            enum variable which needs getter method
	 * @return getter method name
	 */
	public String getEnumMethodName(String variableName) {
		StringBuilder sb = new StringBuilder();
		char[] cArray = variableName.toCharArray();

		for (int i = 0; i < cArray.length - 2; i++)
			sb.append(cArray[i]);

		return sb.toString();
	}

	/**
	 * Generates getter method name for variable
	 * 
	 * @param variableName
	 *            variable name which needs getter method
	 * @return getter method name
	 */
	public String getMethodName(String variableName) {
		StringBuilder sb = new StringBuilder(variableName);
		char[] dst = new char[1];
		sb.getChars(0, 1, dst, 0);
		String methodBeginning = "get" + new String(dst).toUpperCase();
		sb.replace(0, 1, methodBeginning);
		return sb.toString();
	}

	/**
	 * Invokes method for variable and gets its value
	 * 
	 * @param rowData
	 *            Entity with row data
	 * @param methodName
	 *            Method name to invoke for getting value
	 * @return value of Entity field
	 * 
	 * @throws InvocationTargetException
	 * @throw IllegalAccessException
	 * @throws IllegalArgumentExceptio
	 */
	public String invokeMethod(Object rowData, String methodName) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object results = "";
		final Method methods[] = rowData.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; ++i) {
			if (methodName.equals(methods[i].getName())) {
				results = methods[i].invoke(rowData);
			}
		}
		return (results == null) ? "" : results.toString();
	}

	/**
	 * Puts document in store and redirects to user
	 */
	private void redirectExport() {
		//TODO: javaee6
		/*String viewId = Pages.getViewId(FacesContext.getCurrentInstance());
		String baseName = Pages.getCurrentBaseName();
		DocumentType documentType = new DocumentData.DocumentType("xls", "application/vnd.ms-excel");
		DocumentData documentData = new ByteArrayDocumentData(baseName, documentType,
				b.toByteArray());
		String id = DocumentStore.instance().newId();
		String url = DocumentStore.instance().preferredUrlForContent(baseName,
				documentType.getExtension(), id);
		url = Manager.instance().encodeConversationId(url, viewId);
		DocumentStore.instance().saveData(id, documentData);
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			try {
				throw new Exception(Interpolator.instance().interpolate("Could not redirect to #0",
						url), e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}*/
	}

}
