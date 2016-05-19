select count(sub),acc.code as d1, offer.code as d2
from billing_subscription sub
left join billing_user_account ua on ua.id=sub.user_account_id
left join account_entity acc on acc.id=ua.id
left join cat_offer_template offer on offer.id = sub.offer_id
where sub.status='ACTIVE'
and sub.provider_id=#{provider}
and sub.subscription_date <= '#{date}'
group by sub.user_account_id,acc.code,offer.code