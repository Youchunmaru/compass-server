# database

## app operational classes
user has role: 1 - 1
role has permissions: 1 - n
### user
### role
### permission
## app functional classes

event has members(participants): n - m
group has sections: 1 - n

member has group && section: n - 1 && n - m
event has group &| section: n - 1 ^ n - m
accounting has group || section || event: n - 1

### accounting
### event
### group
### member
### section
### details
only references data that requires another table