package org.meveo.service.script.accountingscheme;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.accountingScheme.JournalEntry;
import org.meveo.service.script.Script;

import java.util.ArrayList;
import java.util.Map;

public class NoAccountingEntryScript extends Script {

	private static final long serialVersionUID = 1L;

	@Override
    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("NoAccountingEntryScript EXECUTE context {}", context);
        context.put(Script.RESULT_VALUE, new ArrayList<JournalEntry>());
    }

}
