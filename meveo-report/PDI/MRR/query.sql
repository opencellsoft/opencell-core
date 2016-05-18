SELECT  wo.amount_without_tax/((EXTRACT(epoch FROM (wo.end_date - wo.start_date))/86400)) as mrr_value,
        wo.offer_code as offer_code,ba.code as ba_code,ca.code as ca_code,cust.code as cust_code,
        FROM billing_wallet_operation wo
        LEFT JOIN crm_provider p ON p.id=wo.provider_id
        LEFT JOIN billing_charge_instance ci ON ci.id=wo.charge_instance_id
        LEFT JOIN billing_subscription sub ON sub.id = ci.subscription_id
        LEFT JOIN billing_wallet w ON w.id=wo.wallet_id
        LEFT JOIN billing_user_account uaa ON uaa.id= w.user_account_id
        LEFT JOIN account_entity ua ON ua.id= uaa.id
        LEFT JOIN billing_billing_account baa ON baa.id= uaa.billing_account_id
        LEFT JOIN account_entity ba ON ba.id= baa.id
        LEFT JOIN ar_customer_account caa ON caa.id= baa.customer_account_id
        LEFT JOIN account_entity ca ON ca.id= caa.id
        LEFT JOIN crm_customer custa ON custa.id= caa.customer_id
        LEFT JOIN account_entity cust ON cust.id= custa.id
        WHERE wo.disabled=FALSE
        AND wo.provider_id=#{provider}
        AND NOT(wo.end_date IS NULL)
        AND ((EXTRACT(epoch FROM (wo.end_date - wo.start_date))/86400))>0
        AND (wo.start_date <= '#{date}') AND (wo.end_date > '#{date}')
