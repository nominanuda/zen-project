IdObj @ {id!}
User @ {name!:s,password!:s,hobbies?:[{*}], addr?:@Address}
Address @ {city,country?,line1?,line2?,postCode?}
Blog @ {owner:@IdObj/*of type User*/, title!:s, posts:[@Post]}
Post @ {text,title}