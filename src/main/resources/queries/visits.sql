


select v.post_id, count(distinct v.clientip)
from visits as v
left join seo_robots_agents as ra
on case when ra.isregexp then lower(nullif(v.useragent,'')) ~* lower(ra.useragent)
else lower(nullif(v.useragent,'')) like concat('%',lower(ra.useragent),'%') end
where v.isadmin = false
and ra.id isnull
group by v.post_id
